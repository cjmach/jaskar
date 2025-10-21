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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import java.io.Closeable;

/**
 * Structure consistent with ffi_support ByteBuffer, but zeroized on drop.
 * 
 * @author cmachado
 */
@FieldOrder({"len", "data"})
public class SecretBuffer extends Structure implements Closeable {

    /**
     * must be >= 0, signed int was chosen for compatibility.
     * 
     */
    public long len;

    /**
     * nullable.
     * 
     */
    public Pointer data;
    
    /**
     *
     */
    @Override
    public void close() {
        if (data != Pointer.NULL) {
            Native.free(Pointer.nativeValue(data));
            data = Pointer.NULL;
        }
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

    /**
     *
     */
    public static class ByValue extends SecretBuffer implements Structure.ByValue {
    }

    /**
     *
     */
    public static class ByReference extends SecretBuffer implements Structure.ByReference {
    }
}
