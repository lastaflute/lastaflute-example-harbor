/*
 * Copyright 2015-2022 the original author or authors.
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
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class HarborMultipartRequestHandler implements MultipartRequestHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(HarborMultipartRequestHandler.class);
    public static final long DEFAULT_SIZE_MAX = 250 * 1024 * 1024; // 250MB
    public static final int DEFAULT_SIZE_THRESHOLD = 256 * 1024; // 250KB
    protected static final String CONTEXT_TEMPDIR_KEY = "javax.servlet.context.tempdir";
    protected static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, Object> elementsAll;
    protected Map<String, MultipartFormFile> elementsFile;
    protected Map<String, String[]> elementsText;

    // ===================================================================================
    //                                                                      Handle Request
    //                                                                      ==============
    @Override
    public void handleRequest(HttpServletRequest request) throws ServletException {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // copied from super's method and extends it
        // basically for JVN#14876762
        // thought not all problems are resolved however the main case is safety
        // - - - - - - - - - -/
        final JakartaServletDiskFileUpload upload = createDiskFileUpload(request);
        prepareElementsHash();
        try {
            final List<DiskFileItem> items = parseRequest(request, upload);
            mappingParameter(request, items);
        } catch (FileUploadByteCountLimitException e) {
            handleSizeLimitExceededException(request, e);
        } catch (FileUploadException e) {
            handleFileUploadException(e);
        }
    }

    // ===================================================================================
    //                                                            Create ServletFileUpload
    //                                                            ========================
    protected JakartaServletDiskFileUpload createDiskFileUpload(HttpServletRequest request) {
        final DiskFileItemFactory fileItemFactory = createDiskFileItemFactory();
        final JakartaServletDiskFileUpload upload = newDiskFileUpload(fileItemFactory);
        upload.setHeaderCharset(Charset.forName(request.getCharacterEncoding()));
        upload.setSizeMax(getSizeMax());
        return upload;
    }

    protected JakartaServletDiskFileUpload newDiskFileUpload(DiskFileItemFactory fileItemFactory) {
        return new JakartaServletDiskFileUpload(fileItemFactory) {
            @Override
            public byte[] getBoundary(String contentType) { // for security
                final byte[] boundary = super.getBoundary(contentType);
                checkBoundarySize(contentType, boundary);
                return boundary;
            }
        };
    }

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
        br.addElement("Against for JVN14876762.");
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

    protected DiskFileItemFactory createDiskFileItemFactory() {
        final File repository = createRepositoryFile();
        return DiskFileItemFactory.builder().setFile(repository).setBufferSize(getSizeThreshold()).get();
    }

    protected File createRepositoryFile() {
        return new File(getRepositoryPath());
    }

    // ===================================================================================
    //                                                                      Handling Parts
    //                                                                      ==============
    protected void prepareElementsHash() {
        elementsText = new Hashtable<String, String[]>();
        elementsFile = new Hashtable<String, MultipartFormFile>();
        elementsAll = new Hashtable<String, Object>();
    }

    protected List<DiskFileItem> parseRequest(HttpServletRequest request, JakartaServletDiskFileUpload upload) throws FileUploadException {
        return upload.parseRequest(request);
    }

    protected void mappingParameter(HttpServletRequest request, List<DiskFileItem> items) {
        showFieldLoggingTitle();
        final Iterator<DiskFileItem> iter = items.iterator();
        while (iter.hasNext()) {
            final DiskFileItem item = iter.next();
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

    protected void showFieldLoggingTitle() {
        // logging filter cannot show the parameters when multi-part so logging here
        if (logger.isDebugEnabled()) {
            logger.debug("[Multipart Request Parameter]");
        }
    }

    protected void showFormFieldParameter(DiskFileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}={}", item.getFieldName(), item.getString());
        }
    }

    protected void showFileFieldParameter(DiskFileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}:{name={}, size={}}", item.getFieldName(), item.getName(), item.getSize());
        }
    }

    protected void handleSizeLimitExceededException(HttpServletRequest request, FileUploadByteCountLimitException e) {
        final long actual = e.getActualSize();
        final long permitted = e.getPermitted();
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
    //                                                                            Add Text
    //                                                                            ========
    protected void addTextParameter(HttpServletRequest request, DiskFileItem item) {
        final String name = item.getFieldName();
        final Charset encoding = Charset.forName(request.getCharacterEncoding());
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
                value = item.getString(Charset.forName("ISO-8859-1"));
            } catch (java.io.UnsupportedEncodingException uee) {
                value = item.getString();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to get string from the item: " + item, e);
            }
            haveValue = true;
        }
        if (request instanceof MultipartRequestWrapper) {
            final MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
            wrapper.setParameter(name, value);
        }
        final String[] oldArray = elementsText.get(name);
        final String[] newArray;
        if (oldArray != null) {
            newArray = new String[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = value;
        } else {
            newArray = new String[] { value };
        }
        elementsText.put(name, newArray);
        elementsAll.put(name, newArray);
    }

    protected void addFileParameter(DiskFileItem item) {
        final MultipartFormFile formFile = newActionMultipartFormFile(item);
        elementsFile.put(item.getFieldName(), formFile);
        elementsAll.put(item.getFieldName(), formFile);
    }

    protected ActionMultipartFormFile newActionMultipartFormFile(DiskFileItem item) {
        return new ActionMultipartFormFile(item);
    }

    // ===================================================================================
    //                                                                              Finish
    //                                                                              ======
    @Override
    public void finish() {
        rollback();
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected long getSizeMax() {
        return DEFAULT_SIZE_MAX;
    }

    protected int getSizeThreshold() {
        return DEFAULT_SIZE_THRESHOLD;
    }

    protected String getRepositoryPath() {
        final File tempDirFile = (File) LaServletContextUtil.getServletContext().getAttribute(CONTEXT_TEMPDIR_KEY);
        String tempDir = tempDirFile.getAbsolutePath();
        if (tempDir == null || tempDir.length() == 0) {
            tempDir = System.getProperty(JAVA_IO_TMPDIR_KEY);
        }
        return tempDir;
    }

    // ===================================================================================
    //                                                                           Form File
    //                                                                           =========
    protected static class ActionMultipartFormFile implements MultipartFormFile, Serializable {

        private static final long serialVersionUID = 1L;

        protected final DiskFileItem fileItem;

        public ActionMultipartFormFile(DiskFileItem fileItem) {
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
            try {
                fileItem.delete();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete the fileItem: " + fileItem, e);
            }
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
    public Map<String, Object> getAllElements() {
        return elementsAll;
    }

    @Override
    public Map<String, String[]> getTextElements() {
        return elementsText;
    }

    @Override
    public Map<String, MultipartFormFile> getFileElements() {
        return elementsFile;
    }
}
