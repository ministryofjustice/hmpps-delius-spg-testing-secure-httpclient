package uk.gov.gsi.justice.spg.test.http;

import io.vavr.control.Either;
import uk.gov.gsi.justice.spg.test.model.HttpFault;
import uk.gov.gsi.justice.spg.test.model.HttpSuccess;

import java.io.IOException;

public interface HttpClient {
    Either<HttpFault, HttpSuccess> postTo(String path, String payload) throws IOException;
    Either<HttpFault, HttpSuccess> getFrom(String path) throws IOException;
}