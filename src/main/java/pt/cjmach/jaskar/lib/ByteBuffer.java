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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import java.io.Closeable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author cmachado
 */
@FieldOrder({"len", "data"})
public class ByteBuffer extends Structure implements Closeable {

    /**
     *
     */
    public long len;

    /**
     *
     */
    public Pointer data;

    /**
     *
     */
    public ByteBuffer() {
    }

    /**
     *
     * @param data
     */
    public ByteBuffer(Memory data) {
        this.len = data.size();
        this.data = data;
    }
    
    /**
     *
     * @param data
     */
    public ByteBuffer(byte[] data) {
        this(memoryFromByteArray(data));
    }
    
    /**
     *
     * @param str
     */
    public ByteBuffer(String str) {
        this(memoryFromString(str));
    }
    
    /**
     *
     */
    @Override
    public void close() {
//        if (data != Pointer.NULL) {
//            Native.free(Pointer.nativeValue(data));
//            data = Pointer.NULL;
//            len = 0;
//            write();
//        }
    }
    
    /**
     *
     * @return
     */
    public byte[] getBytes() {
        byte[] bytes = new byte[(int) len];
        data.read(0, bytes, 0, bytes.length);
        return bytes;
    }
    
    static Memory memoryFromByteArray(byte[] bytes) {
        Memory mem = new Memory(bytes.length);
        mem.write(0, bytes, 0, bytes.length);
        return mem;
    }
    
    static Memory memoryFromString(String str) {
        return memoryFromString(str, str.length() + 1, StandardCharsets.US_ASCII);
    }
    
    static Memory memoryFromString(String str, int size, Charset charset) {
        Memory mem = new Memory(size);
        mem.setString(0, str, charset.name());
        return mem;
    }

    /**
     *
     */
    public static class ByValue extends ByteBuffer implements Structure.ByValue {

        /**
         *
         */
        public ByValue() {
        }
        
        /**
         *
         * @param data
         */
        public ByValue(Memory data) {
            super(data);
        }
        
        /**
         *
         * @param data
         */
        public ByValue(byte[] data) {
            super(data);
        }
        
        /**
         *
         * @param data
         */
        public ByValue(String data) {
            super(data);
        }
    }

    /**
     *
     */
    public static class ByReference extends ByteBuffer implements Structure.ByReference {
        
        /**
         *
         */
        public ByReference() {
        }
        
        /**
         *
         * @param data
         */
        public ByReference(Memory data) {
            super(data);
        }
        
        /**
         *
         * @param data
         */
        public ByReference(byte[] data) {
            super(data);
        }
        
        /**
         *
         * @param data
         */
        public ByReference(String data) {
            super(data);
        }
    }
}
