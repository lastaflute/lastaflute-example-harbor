/*
 * Copyright 2015-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.mylasta.direction.sponsor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dbflute.helper.message.ExceptionMessageBuilder;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.exception.Forced404NotFoundException;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.ruts.multipart.MultipartRequestHandler;
import org.lastaflute.web.ruts.multipart.MultipartRequestWrapper;
import org.lastaflute.web.ruts.multipart.exception.MultipartExceededException;
import org.lastaflute.web.util.LaServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The handler of multipart request (fileupload request). <br>
 * This instance is created per one multipart request.
 * @author modified by jflute (originated in Seasar)
 */
public class HarborMultipartRequestHandler implements MultipartRequestHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(HarborMultipartRequestHandler.class);

    // -----------------------------------------------------
    //                                          Limit Option
    //                                          ------------
    // you can adjust here
    public static final long DEFAULT_SIZE_MAX = 250 * 1024 * 1024; // is 250MB, total size per one request
    public static final int DEFAULT_SIZE_THRESHOLD = 256 * 1024; // is 250KB, momory or file, per one parameter
    public static final long DEFAULT_FILE_COUNT_MAX = 300; // total files per one request (contains normal parameters)

    // -----------------------------------------------------
    //                                   Temporary Directory
    //                                   -------------------
    // used as repository for requested parameters
    protected static final String CONTEXT_TEMPDIR_KEY = "javax.servlet.context.tempdir"; // prior
    protected static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir"; // secondary

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // keeping parsed request parameters, normal texts or uploaded files
    // keys are requested parameter names (treated as field name here)
    protected Map<String, Object> elementsAll; // lazy-loaded, then after not null
    protected Map<String, MultipartFormFile> elementsFile; // me too
    protected Map<String, String[]> elementsText; // me too

    // ===================================================================================
    //                                                                      Handle Request
    //                                                                      ==============
    @Override
    public void handleRequest(HttpServletRequest request) throws ServletException {
        final ServletFileUpload upload = createServletFileUpload(request);
        prepareElementsHash();
        try {
            final List<FileItem> items = parseRequest(request, upload);
            mappingParameter(request, items);
        } catch (SizeLimitExceededException e) { // special handling
            handleSizeLimitExceededException(request, e);
        } catch (FileUploadException e) { // contains fileCount exceeded
            handleFileUploadException(e);
        }
    }

    protected void prepareElementsHash() { // traditional name
        // #thinking jflute might lazy-loaded be unneeded? because created per request (2024/09/08)
        elementsAll = new HashMap<String, Object>();
        elementsText = new HashMap<String, String[]>();
        elementsFile = new HashMap<String, MultipartFormFile>();
    }

    protected List<FileItem> parseRequest(HttpServletRequest request, ServletFileUpload upload) throws FileUploadException {
        return upload.parseRequest(request);
    }

    // ===================================================================================
    //                                                                   ServletFileUpload
    //                                                                   =================
    protected ServletFileUpload createServletFileUpload(HttpServletRequest request) {
        final DiskFileItemFactory fileItemFactory = createDiskFileItemFactory();
        final ServletFileUpload upload = newServletFileUpload(fileItemFactory);
        setupServletFileUpload(upload, request);
        return upload;
    }

    // -----------------------------------------------------
    //                          DiskFileItemFactory Settings
    //                          ----------------------------
    protected DiskFileItemFactory createDiskFileItemFactory() {
        final int sizeThreshold = getSizeThreshold();
        final File repository = createRepositoryFile();
        return new DiskFileItemFactory(sizeThreshold, repository);
    }

    protected int getSizeThreshold() {
        return DEFAULT_SIZE_THRESHOLD;
    }

    protected File createRepositoryFile() {
        return new File(getRepositoryPath());
    }

    protected String getRepositoryPath() {
        final ServletContext servletContext = LaServletContextUtil.getServletContext();
        final File tempDirFile = (File) servletContext.getAttribute(CONTEXT_TEMPDIR_KEY);
        String tempDir = tempDirFile.getAbsolutePath();
        if (tempDir == null || tempDir.length() == 0) {
            tempDir = System.getProperty(JAVA_IO_TMPDIR_KEY);
        }
        return tempDir; // must be not null
    }

    // -----------------------------------------------------
    //                            ServletFileUpload Settings
    //                            --------------------------
    protected ServletFileUpload newServletFileUpload(DiskFileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory) {
            @Override
            protected byte[] getBoundary(String contentType) { // for security
                final byte[] boundary = super.getBoundary(contentType);
                checkBoundarySize(contentType, boundary);
                return boundary;
            }
        };
    }

    // #for_now jflute to suppress CVE-2014-0050 even if commons-fileupload is older than safety version (2024/09/08)
    // but if you use safety version, this extension is basically unneeded (or you can use it as double check)
    protected void checkBoundarySize(String contentType, byte[] boundary) {
        final int boundarySize = boundary.length;
        final int limitSize = getBoundaryLimitSize();
        if (boundarySize > getBoundaryLimitSize()) {
            throwTooLongBoundarySizeException(contentType, boundarySize, limitSize);
        }
    }

    protected int getBoundaryLimitSize() {
        // one HTTP proxy tool already limits the size (e.g. 3450 bytes)
        // so specify this size for test
        return 2000; // you can override as you like it
    }

    protected void throwTooLongBoundarySizeException(String contentType, int boundarySize, int limitSize) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Too long boundary size so treats it as 404.");
        br.addItem("Advice");
        br.addElement("Against for CVE-2014-0050 (JVN14876762).");
        br.addElement("Boundary size is limited by Framework.");
        br.addElement("Too long boundary is treated as 404 because it's thought of as attack.");
        br.addElement("");
        br.addElement("While, you can override the boundary limit size");
        br.addElement(" in " + getClass().getSimpleName() + ".");
        br.addItem("Content Type");
        br.addElement(contentType);
        br.addItem("Boundary Size");
        br.addElement(boundarySize);
        br.addItem("Limit Size");
        br.addElement(limitSize);
        final String msg = br.buildExceptionMessage();
        throw new Forced404NotFoundException(msg, UserMessages.empty()); // heavy attack!? so give no page to tell wasted action
    }

    protected void setupServletFileUpload(ServletFileUpload upload, HttpServletRequest request) {
        upload.setHeaderEncoding(request.getCharacterEncoding());
        upload.setSizeMax(getSizeMax());
        upload.setFileCountMax(getFileCountMax()); // since commons-fileupload-1.5
    }

    protected long getSizeMax() {
        return DEFAULT_SIZE_MAX;
    }

    protected long getFileCountMax() {
        return DEFAULT_FILE_COUNT_MAX;
    }

    // ===================================================================================
    //                                                                   Parameter Mapping
    //                                                                   =================
    protected void mappingParameter(HttpServletRequest request, List<FileItem> items) {
        showFieldLoggingTitle();
        final Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            final FileItem item = iter.next();
            if (item.isFormField()) {
                showFormFieldParameter(item);
                addTextParameter(request, item);
            } else {
                showFileFieldParameter(item);
                final String itemName = item.getName();
                if (itemName != null && !itemName.isEmpty()) {
                    addFileParameter(item);
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                     Parameter Logging
    //                                     -----------------
    // logging filter cannot show the parameters when multi-part so logging here
    protected void showFieldLoggingTitle() {
        if (logger.isDebugEnabled()) {
            logger.debug("[Multipart Request Parameter]");
        }
    }

    protected void showFormFieldParameter(FileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}={}", item.getFieldName(), item.getString());
        }
    }

    protected void showFileFieldParameter(FileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}:{name={}, size={}}", item.getFieldName(), item.getName(), item.getSize());
        }
    }

    // ===================================================================================
    //                                                                       Add Parameter
    //                                                                       =============
    protected void addTextParameter(HttpServletRequest request, FileItem item) {
        final String fieldName = item.getFieldName();
        final String encoding = request.getCharacterEncoding();
        String value = null;
        boolean haveValue = false;
        if (encoding != null) {
            try {
                value = item.getString(encoding);
                haveValue = true;
            } catch (Exception e) {}
        }
        if (!haveValue) {
            try {
                value = item.getString("ISO-8859-1");
            } catch (java.io.UnsupportedEncodingException uee) {
                value = item.getString();
            }
            haveValue = true;
        }
        if (request instanceof MultipartRequestWrapper) {
            final MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
            wrapper.setParameter(fieldName, value);
        }
        final String[] oldArray = elementsText.get(fieldName);
        final String[] newArray;
        if (oldArray != null) {
            newArray = new String[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = value;
        } else {
            newArray = new String[] { value };
        }
        elementsAll.put(fieldName, newArray);
        elementsText.put(fieldName, newArray);
    }

    protected void addFileParameter(FileItem item) {
        final String fieldName = item.getFieldName();
        final MultipartFormFile formFile = newActionMultipartFormFile(item);
        elementsAll.put(fieldName, formFile);
        elementsFile.put(fieldName, formFile);
    }

    protected ActionMultipartFormFile newActionMultipartFormFile(FileItem item) {
        return new ActionMultipartFormFile(item);
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    protected void handleSizeLimitExceededException(HttpServletRequest request, SizeLimitExceededException e) {
        final long actual = e.getActualSize();
        final long permitted = e.getPermittedSize();
        String msg = "Exceeded size of the multipart request: actual=" + actual + " permitted=" + permitted;
        request.setAttribute(MAX_LENGTH_EXCEEDED_KEY, new MultipartExceededException(msg, actual, permitted, e));
        try {
            final InputStream is = request.getInputStream();
            try {
                final byte[] buf = new byte[1024];
                @SuppressWarnings("unused")
                int len = 0;
                while ((len = is.read(buf)) != -1) {}
            } catch (Exception ignored) {} finally {
                try {
                    is.close();
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    protected void handleFileUploadException(FileUploadException e) throws ServletException {
        // suppress logging because it can be caught by logging filter 
        //log.error("Failed to parse multipart request", e);
        throw new ServletException("Failed to upload the file.", e);
    }

    // ===================================================================================
    //                                                                           Roll-back
    //                                                                           =========
    @Override
    public void rollback() {
        final Iterator<MultipartFormFile> iter = elementsFile.values().iterator();
        while (iter.hasNext()) {
            final MultipartFormFile formFile = iter.next();
            formFile.destroy();
        }
    }

    // ===================================================================================
    //                                                                              Finish
    //                                                                              ======
    @Override
    public void finish() {
        rollback();
    }

    // ===================================================================================
    //                                                                           Form File
    //                                                                           =========
    protected static class ActionMultipartFormFile implements MultipartFormFile, Serializable {

        private static final long serialVersionUID = 1L;

        protected final FileItem fileItem;

        public ActionMultipartFormFile(FileItem fileItem) {
            this.fileItem = fileItem;
        }

        public byte[] getFileData() throws IOException {
            return fileItem.get();
        }

        public InputStream getInputStream() throws IOException {
            return fileItem.getInputStream();
        }

        public String getContentType() {
            return fileItem.getContentType();
        }

        public int getFileSize() {
            return (int) fileItem.getSize();
        }

        public String getFileName() {
            return getBaseFileName(fileItem.getName());
        }

        protected String getBaseFileName(String filePath) {
            final String fileName = new File(filePath).getName();
            int colonIndex = fileName.indexOf(":");
            if (colonIndex == -1) {
                colonIndex = fileName.indexOf("\\\\"); // Windows SMB
            }
            final int backslashIndex = fileName.lastIndexOf("\\");
            if (colonIndex > -1 && backslashIndex > -1) {
                return fileName.substring(backslashIndex + 1);
            } else {
                return fileName;
            }
        }

        public void destroy() {
            fileItem.delete();
        }

        @Override
        public String toString() {
            return "formFile:{" + getFileName() + "}";
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public Map<String, Object> getAllElements() { // not null after parsing
        return elementsAll;
    }

    @Override
    public Map<String, String[]> getTextElements() { // me too
        return elementsText;
    }

    @Override
    public Map<String, MultipartFormFile> getFileElements() { // me too
        return elementsFile;
    }
}
