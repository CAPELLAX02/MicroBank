package com.microbank.document.service;

import java.io.InputStream;

public interface MinIOService {

    String uploadFile(String fileName, InputStream fileStream, String contentType);

}
