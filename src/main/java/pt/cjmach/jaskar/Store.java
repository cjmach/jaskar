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

import pt.cjmach.jaskar.lib.StringList;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import pt.cjmach.jaskar.lib.AskarCallback;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ByteBuffer;
import pt.cjmach.jaskar.lib.ErrorCode;
import pt.cjmach.jaskar.lib.SizeT;

/**
 * An instance of an opened store.
 * 
 * @author cmachado
 */
public class Store implements Closeable {

    private final SizeT handle;
    private final String uri;

    /**
     * 
     * @param handle
     * @param uri 
     */
    Store(SizeT handle, String uri) {
        this.handle = handle;
        this.uri = uri;
    }

    /**
     * Close the store instance, waiting for any shutdown procedures to complete.
     * 
     * @throws IOException 
     */
    @Override
    public void close() throws IOException {
        try {
            close(false);
        } catch (AskarException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Close the store instance, waiting for any shutdown procedures to complete.
     * 
     * @param alsoRemove
     * @return
     * @throws AskarException 
     */
    public boolean close(boolean alsoRemove) throws AskarException {
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_store_close(handle, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            if (alsoRemove) {
                return remove(uri);
            }
            return true;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Copy to a new store instance using a database URL.
     * 
     * @param toStore
     * @param fromProfile
     * @param toProfile
     * @throws AskarException 
     */
    public void copyProfile(Store toStore, String fromProfile, String toProfile) throws AskarException {
        Objects.requireNonNull(toStore);
        Objects.requireNonNull(fromProfile);
        Objects.requireNonNull(toProfile);
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_store_copy_profile(handle, toStore.handle, fromProfile, toProfile, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Copy to a new store instance using a database URL.
     * 
     * @param uri
     * @param method
     * @param passKey
     * @param recreate
     * @return
     * @throws AskarException 
     */
    public Store copyTo(String uri, StoreKeyMethod method, String passKey, boolean recreate) throws AskarException {
        Objects.requireNonNull(uri);
        Objects.requireNonNull(method);
        Objects.requireNonNull(passKey);
        AskarCallback.SizeT callback = new AskarCallback.SizeT();
        ErrorCode errorCode = AskarLibrary.askar_store_copy(handle, uri, method.getMethod(), passKey, (byte) (recreate ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            SizeT storeHandle = callback.getSizeT();
            return new Store(storeHandle, uri);
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Create a new profile with the given profile name.
     * 
     * @param name
     * @return
     * @throws AskarException 
     */
    public String createProfile(String name) throws AskarException {
        Objects.requireNonNull(name);
        AskarCallback.String callback = new AskarCallback.String();
        ErrorCode errorCode = AskarLibrary.askar_store_create_profile(handle, name, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            String result = callback.getString();
            return result;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Get the default profile name used when opening the store.
     * 
     * @return
     * @throws AskarException 
     */
    public String getDefaultProfile() throws AskarException {
        AskarCallback.String callback = new AskarCallback.String();
        ErrorCode errorCode = AskarLibrary.askar_store_get_default_profile(handle, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            String profile = callback.getString();
            return profile;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Get the database URL of this store.
     * 
     * @return 
     */
    public String getUri() {
        return uri;
    }

    /**
     * Get the details of all store profiles.
     * 
     * @return
     * @throws AskarException 
     */
    public String[] listProfiles() throws AskarException {
        AskarCallback.Pointer callback = new AskarCallback.Pointer();
        ErrorCode errorCode = AskarLibrary.askar_store_list_profiles(handle, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Pointer results = callback.getPointer();
            if (results == Pointer.NULL) {
                return null;
            }
            try (StringList list = new StringList(results)) {
                return list.toArray();
            }
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Create a new session against the store.
     * 
     * @return
     * @throws AskarException
     */
    public Session openSession() throws AskarException {
        return openSession(getDefaultProfile(), false);
    }

    /**
     * Create a new session against the store.
     * 
     * @param isTransaction
     * @return
     * @throws AskarException
     */
    public Session openSession(boolean isTransaction) throws AskarException {
        return openSession(getDefaultProfile(), isTransaction);
    }

    /**
     * Create a new session against the store.
     * 
     * @param profile
     * @param isTransaction
     * @return
     * @throws AskarException
     */
    public Session openSession(String profile, boolean isTransaction) throws AskarException {
        AskarCallback.SizeT callback = new AskarCallback.SizeT();
        ErrorCode errorCode = AskarLibrary.askar_session_start(handle, profile, (byte) (isTransaction ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            SizeT sessionHandle = callback.getSizeT();
            Session session = new Session(sessionHandle, isTransaction);
            return session;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Replace the wrapping key on a store.
     * 
     * @param method
     * @param passKey
     * @throws AskarException
     */
    public void rekey(StoreKeyMethod method, String passKey) throws AskarException {
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_store_rekey(handle, method.getMethod(), passKey, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Remove an existing profile with the given profile namestore.
     * 
     * @param name
     * @return
     * @throws AskarException
     */
    public boolean removeProfile(String name) throws AskarException {
        Objects.requireNonNull(name);
        AskarCallback.Byte callback = new AskarCallback.Byte();
        ErrorCode errorCode = AskarLibrary.askar_store_remove_profile(handle, name, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            boolean removed = callback.getBoolean();
            return removed;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Change the name of an existing profile.
     * 
     * @param oldName
     * @param newName
     * @return
     * @throws AskarException
     */
    public boolean renameProfile(String oldName, String newName) throws AskarException {
        Objects.requireNonNull(oldName);
        Objects.requireNonNull(newName);
        AskarCallback.Byte callback = new AskarCallback.Byte();
        ErrorCode errorCode = AskarLibrary.askar_store_rename_profile(handle, oldName, newName, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            boolean renamed = callback.getBoolean();
            return renamed;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
    
    /**
     * Create a new scan instance against the store.
     * 
     * @param profile
     * @param category
     * @param tagFilter
     * @param offset
     * @param limit
     * @param orderBy
     * @param descending
     * @return a new {@link Scan} instance. The result will keep an open connection to the backend until it is consumed.
     * @throws AskarException
     */
    public Scan scanStart(String profile, String category, String tagFilter, long offset, long limit, String orderBy, boolean descending) throws AskarException {
        AskarCallback.SizeT callback = new AskarCallback.SizeT();
        ErrorCode errorCode = AskarLibrary.askar_scan_start(handle, profile, category, tagFilter, offset, limit, orderBy, (byte)(descending ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            SizeT scanHandle = callback.getSizeT();
            Scan scan = new Scan(scanHandle);
            return scan;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Set the default profile name used when opening the store.
     * 
     * @param name
     * @throws AskarException
     */
    public void setDefaultProfile(String name) throws AskarException {
        Objects.requireNonNull(name);
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_store_set_default_profile(handle, name, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Generate a new raw store key.
     * 
     * @param seed
     * @return
     * @throws AskarException
     */
    public static String generateRawKey(String seed) throws AskarException {
        try (ByteBuffer.ByValue bufferSeed = new ByteBuffer.ByValue(seed)) {
            return generateRawKey(bufferSeed);
        }
    }
    
    public static String generateRawKey() throws AskarException {
        try (ByteBuffer.ByValue seed = new ByteBuffer.ByValue()) {
            return generateRawKey(seed);
        }
    }
    
    private static String generateRawKey(ByteBuffer.ByValue seed) throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_store_generate_raw_key(seed, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String result = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return result;
    }

    /**
     * Open a store instance from a database URL.
     * 
     * @param specUri
     * @param keyMethod
     * @param passKey
     * @param profile
     * @return
     * @throws AskarException
     */
    public static Store open(String specUri, StoreKeyMethod keyMethod, String passKey, String profile) throws AskarException {
        Objects.requireNonNull(specUri);
        Objects.requireNonNull(keyMethod);
        Objects.requireNonNull(passKey);
        AskarCallback.SizeT callback = new AskarCallback.SizeT();
        ErrorCode errorCode = AskarLibrary.askar_store_open(specUri, keyMethod.getMethod(), passKey, profile, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Store store = new Store(callback.getSizeT(), specUri);
            return store;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Provision a new store instance using a database URL.
     * 
     * @param specUri
     * @param keyMethod
     * @param passKey
     * @param profile
     * @param recreate
     * @return
     * @throws AskarException
     */
    public static Store provision(String specUri, StoreKeyMethod keyMethod, String passKey, String profile, boolean recreate) throws AskarException {
        Objects.requireNonNull(specUri);
        Objects.requireNonNull(keyMethod);
        Objects.requireNonNull(passKey);
        AskarCallback.SizeT callback = new AskarCallback.SizeT();
        ErrorCode errorCode = AskarLibrary.askar_store_provision(specUri, keyMethod.getMethod(), passKey, profile, (byte) (recreate ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Store store = new Store(callback.getSizeT(), specUri);
            return store;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Remove a store instance using a database URL.
     * 
     * @param uri
     * @return
     * @throws AskarException
     */
    public static boolean remove(String uri) throws AskarException {
        AskarCallback.Byte callback = new AskarCallback.Byte();
        ErrorCode errorCode = AskarLibrary.askar_store_remove(uri, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            boolean removed = callback.getBoolean();
            return removed;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
}
