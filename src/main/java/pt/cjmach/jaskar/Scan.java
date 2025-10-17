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
package pt.cjmach.jaskar;

import com.sun.jna.Pointer;
import java.io.Closeable;
import pt.cjmach.jaskar.lib.AskarCallback;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ErrorCode;
import pt.cjmach.jaskar.lib.SizeT;

/**
 *
 * @author cmachado
 */
public class Scan implements Closeable {
    private SizeT handle;
    
    /**
     * 
     * @param handle 
     */
    Scan(SizeT handle) {
        this.handle = handle;
    }
    
    /**
     * 
     */
    @Override
    public void close() {
        if (handle != null) {
            AskarLibrary.askar_scan_free(handle);
            handle = null;
        }
    }
    
    /**
     * 
     * @return
     * @throws AskarException 
     */
    public Entry next() throws AskarException {
        AskarCallback.Pointer callback = new AskarCallback.Pointer();
        ErrorCode errorCode = AskarLibrary.askar_scan_next(handle, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Pointer p = callback.getPointer();
            if (p == Pointer.NULL) {
                return null;
            }
            Entry entry = new Entry(p, 0);
            return entry;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
}
