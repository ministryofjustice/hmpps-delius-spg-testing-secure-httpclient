package uk.gov.gsi.justice.spg.test.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface HttpInterface {
    @POST
    Call<ResponseBody> post(@Url String url, @Body String body);

    @GET
    Call<ResponseBody> get(@Url String url);
}
