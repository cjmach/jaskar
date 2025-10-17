/*
 *  Copyright 2025 Carlos Machado
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package pt.cjmach.jaskar.lib;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author cmachado
 */
public class AskarCallback {

    private final CountDownLatch latch;
    private final long id;

    /**
     *
     */
    protected ErrorCode errorCode;

    /**
     *
     */
    protected AskarCallback() {
        latch = new CountDownLatch(1);
        id = Thread.currentThread().getId();
        errorCode = ErrorCode.CUSTOM;
    }
    
    /**
     *
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        latch.await();
    }

    /**
     *
     */
    protected void countDown() {
        latch.countDown();
    }

    /**
     *
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     *
     */
    public static class Basic extends AskarCallback implements AskarLibrary.BasicCallback {

        /**
         *
         * @param cb_id
         * @param err
         */
        @Override
        public void invoke(long cb_id, ErrorCode err) {
            assert cb_id == getId();
            this.errorCode = err;
            countDown();
        }
    }

    /**
     *
     */
    public static class Byte extends AskarCallback implements AskarLibrary.ByteCallback {

        private byte byteResult = -1;

        /**
         *
         * @return
         */
        public byte getByte() {
            return byteResult;
        }
        
        /**
         *
         * @return
         */
        public boolean getBoolean() {
            return byteResult != 0;
        }

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        @Override
        public void invoke(long cb_id, ErrorCode err, byte result) {
            assert cb_id == getId();
            this.errorCode = err;
            this.byteResult = result;
            countDown();
        }
    }

    /**
     *
     */
    public static class Long extends AskarCallback implements AskarLibrary.LongCallback {

        private long longResult = -1;

        /**
         *
         * @return
         */
        public long getLong() {
            return longResult;
        }

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        @Override
        public void invoke(long cb_id, ErrorCode err, long result) {
            assert cb_id == getId();
            this.errorCode = err;
            this.longResult = result;
            countDown();
        }
    }

    /**
     *
     */
    public static class Pointer extends AskarCallback implements AskarLibrary.PointerCallback {

        private com.sun.jna.Pointer pointerResult = com.sun.jna.Pointer.NULL;

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        @Override
        public void invoke(long cb_id, ErrorCode err, com.sun.jna.Pointer result) {
            assert cb_id == getId();
            this.errorCode = err;
            this.pointerResult = result;
            countDown();
        }

        /**
         *
         * @return
         */
        public com.sun.jna.Pointer getPointer() {
            return pointerResult;
        }
    }

    /**
     *
     */
    public static class SizeT extends AskarCallback implements AskarLibrary.SizeTCallback {

        private pt.cjmach.jaskar.lib.SizeT sizeTResult;

        /**
         *
         * @return
         */
        public pt.cjmach.jaskar.lib.SizeT getSizeT() {
            return sizeTResult;
        }

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        @Override
        public void invoke(long cb_id, ErrorCode err, pt.cjmach.jaskar.lib.SizeT result) {
            assert cb_id == getId();
            this.errorCode = err;
            this.sizeTResult = result;
            countDown();
        }
    }

    /**
     *
     */
    public static class String extends AskarCallback implements AskarLibrary.StringCallback {
        private java.lang.String stringResult;

        /**
         *
         * @return
         */
        public java.lang.String getString() {
            return stringResult;
        }

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        @Override
        public void invoke(long cb_id, ErrorCode err, java.lang.String result) {
            assert cb_id == getId();
            this.errorCode = err;
            this.stringResult = result;
            countDown();
        }
    }

}
