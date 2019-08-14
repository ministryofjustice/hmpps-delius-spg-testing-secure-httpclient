package uk.gov.gsi.justice.spg.test.model;

import java.util.Objects;

public class HttpSuccess {
    private final Integer code;
    private final String body;

    public HttpSuccess(Integer code, String body) {
        this.code = code;
        this.body = body;
    }

    public Integer getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpSuccess that = (HttpSuccess) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, body);
    }

    @Override
    public String toString() {
        return "HttpSuccess{" +
                "code=" + code +
                ", body='" + body + '\'' +
                '}';
    }
}