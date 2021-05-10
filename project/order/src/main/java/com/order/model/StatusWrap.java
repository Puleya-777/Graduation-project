package com.order.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class StatusWrap {
    private final Integer errno;
    private final String errmsg;
    private final Object data;

    private StatusWrap(Object data, Status err) {
        this.errno = err.getErrno();
        this.errmsg = err.getErrmsg();
        this.data = data;
    }

    private ResponseEntity<StatusWrap> response(HttpStatus status) {
        if (status == null) {
            status = HttpStatus.OK;
            if (errno.equals(Status.RESOURCE_ID_NOTEXIST.getErrno())) {
                status = HttpStatus.NOT_FOUND;
            } else if (errno.equals(Status.FIELD_NOTVALID.getErrno())) {
                status = HttpStatus.BAD_REQUEST;
            } else if (errno.equals(Status.INTERNAL_SERVER_ERR.getErrno())) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            } else if (errno.equals(Status.RESOURCE_ID_OUTSCOPE.getErrno())) {
                status = HttpStatus.FORBIDDEN;
            } else if (errno.equals(Status.LOGIN_REQUIRED.getErrno())) {
                status = HttpStatus.UNAUTHORIZED;
            }
        }
        return new ResponseEntity<>(this, status);
    }


    private ResponseEntity<StatusWrap> response() {
        return this.response(null);
    }

    public static ResponseEntity<StatusWrap> of(@NotNull Object data, HttpStatus status) {
        return new StatusWrap(data, Status.OK).response(status);
    }

    public static ResponseEntity<StatusWrap> of(@NotNull Object data) {
        return new StatusWrap(data, Status.OK).response();
    }

    public static ResponseEntity<StatusWrap> of(@NotNull PageInfo<?> page) {
        return new StatusWrap(new PageWrap(page), Status.OK).response();
    }

    public static ResponseEntity<StatusWrap> just(@NotNull Status status) {
        return new StatusWrap(null, status).response();
    }

    public static ResponseEntity<StatusWrap> ok(HttpStatus status) {
        return new StatusWrap(null, Status.OK).response(status);
    }

    public static ResponseEntity<StatusWrap> ok() {
        return new StatusWrap(null, Status.OK).response();
    }

    @Getter
    private static class PageWrap {
        private final Integer page;
        private final Integer pageSize;
        private final Long total;
        private final Integer pages;
        private final List<?> list;

        public PageWrap(PageInfo<?> raw) {
            page = raw.getPageNum();
            pageSize = raw.getPageSize();
            total = raw.getTotal();
            pages = raw.getPages();
            list = raw.getList();
        }
    }
}
