package uk.gov.gsi.justice.spg.test.model;

import java.util.Objects;

public class HttpFault {
    private final Integer code;
    private final String message;

    public HttpFault(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpFault httpFault = (HttpFault) o;
        return Objects.equals(code, httpFault.code) &&
                Objects.equals(message, httpFault.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "HttpFault{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}