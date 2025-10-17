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

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 *
 * @author cmachado
 */
public enum ErrorCode {

    /**
     *
     */
    SUCCESS(0),

    /**
     *
     */
    BACKEND(1),

    /**
     *
     */
    BUSY(2),

    /**
     *
     */
    DUPLICATE(3),

    /**
     *
     */
    ENCRYPTION(4),

    /**
     *
     */
    INPUT(5),

    /**
     *
     */
    NOT_FOUND(6),

    /**
     *
     */
    UNEXPECTED(7),

    /**
     *
     */
    UNSUPPORTED(8),

    /**
     *
     */
    CUSTOM(100);

    private final long code;

    private ErrorCode(long code) {
        this.code = code;
    }

    long getCode() {
        return code;
    }
    
    /**
     *
     * @param code
     * @return
     */
    public static ErrorCode fromCode(int code) {
        switch (code) {
            case 0:
                return SUCCESS;
            case 1:
                return BACKEND;
            case 2:
                return BUSY;
            case 3:
                return DUPLICATE;
            case 4:
                return ENCRYPTION;
            case 5:
                return INPUT;
            case 6:
                return NOT_FOUND;
            case 7:
                return UNEXPECTED;
            case 8:
                return UNSUPPORTED;
            case 100:
                return CUSTOM;
            default:
                throw new IllegalArgumentException("Unknown error code " + code);
        }
    }
    
    static class Converter implements TypeConverter {

        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            long value = (long) nativeValue;
            return fromCode((int) value);
        }

        @Override
        public Class<?> nativeType() {
            return Long.class;
        }

        @Override
        public Object toNative(Object value, ToNativeContext context) {
            return ((ErrorCode) value).getCode();
        }
        
    }
}
