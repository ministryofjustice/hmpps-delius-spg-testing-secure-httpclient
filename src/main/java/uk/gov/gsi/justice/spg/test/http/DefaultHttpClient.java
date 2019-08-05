package uk.gov.gsi.justice.spg.test.http;

import io.vavr.control.Either;
import io.vavr.control.Try;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import uk.gov.gsi.justice.spg.test.model.HttpFault;
import uk.gov.gsi.justice.spg.test.model.HttpSuccess;

public class DefaultHttpClient implements HttpClient {
    private final HttpInterface httpInterface;

    public DefaultHttpClient(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    @Override
    public Either<HttpFault, HttpSuccess> postTo(String path, String payload) {
        final Call<ResponseBody> call = httpInterface.post(path, payload);

        return Try.of(call::execute)
                .fold(
                        l -> Either.left(new HttpFault(0, l.getMessage())),
                        this::processResponse
                );
    }

    @Override
    public Either<HttpFault, HttpSuccess> getFrom(String path) {
        final Call<ResponseBody> call = httpInterface.get(path);

        return Try.of(call::execute)
                .fold(
                        l -> Either.left(new HttpFault(0, l.getMessage())),
                        this::processResponse
                );
    }

    private Either<HttpFault, HttpSuccess> processResponse(Response<ResponseBody> response) {
        return response.isSuccessful() ?
                parseResponseBody(response.code(), response.body()) :
                parseResponseBody(response.code(), response.errorBody());
    }

    private Either<HttpFault, HttpSuccess> parseResponseBody(Integer code, ResponseBody responseBody) {
        return Try.of(responseBody::string)
                .fold(
                        l -> Either.left(new HttpFault(code, l.getMessage())),
                        r -> Either.right(new HttpSuccess(code, r))
                );
    }
}