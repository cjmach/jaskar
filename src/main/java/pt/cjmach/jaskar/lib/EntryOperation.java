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
 * Supported operations for entries in the store.
 * 
 * @author cmachado
 */
public enum EntryOperation {

    /**
     * Insert a new {@link pt.cjmach.jaskar.Entry}.
     */
    INSERT((byte) 0),

    /**
     * Replace an existing {@link pt.cjmach.jaskar.Entry}.
     */
    REPLACE((byte) 1),

    /**
     * Replace an existing {@link pt.cjmach.jaskar.Entry}.
     */
    REMOVE((byte) 2);
    
    private final byte operation;

    private EntryOperation(byte operation) {
        this.operation = operation;
    }

    /**
     *
     * @return
     */
    byte getOperation() {
        return operation;
    }
    
    static EntryOperation fromOperation(byte operation) {
        switch (operation) {
            case (byte) 0:
                return INSERT;
            case (byte) 1:
                return REPLACE;
            case (byte) 2:
                return REMOVE;
            default:
                throw new IllegalArgumentException("Illegal operation " + operation);
        }
    }
    
    static class Converter implements TypeConverter {

        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            byte value = (byte) nativeValue;
            return fromOperation(value);
        }

        @Override
        public Class<?> nativeType() {
            return Byte.class;
        }

        @Override
        public Object toNative(Object value, ToNativeContext context) {
            return ((EntryOperation) value).getOperation();
        }
        
    }
}
