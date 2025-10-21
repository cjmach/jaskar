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
import java.io.IOException;
import java.util.Objects;
import pt.cjmach.jaskar.lib.AskarCallback;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ByteBuffer;
import pt.cjmach.jaskar.lib.EntryOperation;
import pt.cjmach.jaskar.lib.ErrorCode;
import pt.cjmach.jaskar.lib.SizeT;

/**
 * An active connection to the store backend.
 * 
 * @author cmachado
 */
public class Session implements Closeable {

    private SizeT handle;
    private final boolean isTransaction;

    /**
     * 
     * @param handle
     * @param isTransaction 
     */
    Session(SizeT handle, boolean isTransaction) {
        this.handle = handle;
        this.isTransaction = isTransaction;
    }

    /**
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
     * 
     * @param commit
     * @throws AskarException 
     */
    public void close(boolean commit) throws AskarException {
        if (handle != null) {
            AskarCallback.Basic callback = new AskarCallback.Basic();
            ErrorCode errorCode = AskarLibrary.askar_session_close(handle, (byte) (commit ? 1 : 0), callback, callback.getId());
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            
            handle = null;
            try {
                callback.await();
            } catch (InterruptedException ex) {
                throw new AskarException(ex);
            }
        }
    }
    
    /**
     * Commit the pending transaction.
     * 
     * @throws AskarException 
     */
    public void commit() throws AskarException {
        Objects.requireNonNull(handle, "Cannot commit a closed session.");
        if (!isTransaction) {
            throw new IllegalStateException("Session is not a transation.");
        }
        close(true);
    }
    
    /**
     * Count the number of entries for a given record category.
     * 
     * @param category
     * @param tagFilter
     * @return
     * @throws AskarException 
     */
    public long count(String category, String tagFilter) throws AskarException {
        Objects.requireNonNull(handle, "Cannot count from a closed session.");
        AskarCallback.Long callback = new AskarCallback.Long();
        ErrorCode errorCode = AskarLibrary.askar_session_count(handle, category, tagFilter, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            long result = callback.getLong();
            return result;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Retrieve the current record at '(category, name)'.
     * 
     * @param category
     * @param name
     * @param forUpdate Set to {@code true} when in a transaction to create an update lock on the associated record, if supported by the store backend.
     * @return
     * @throws AskarException 
     */
    public Entry fetch(String category, String name, boolean forUpdate) throws AskarException {
        Objects.requireNonNull(handle, "Cannot fetch from a closed session.");
        Objects.requireNonNull(category);
        Objects.requireNonNull(name);
        AskarCallback.Pointer callback = new AskarCallback.Pointer();
        ErrorCode errorCode = AskarLibrary.askar_session_fetch(handle, category, name, (byte) (forUpdate ? 1 : 0), callback, callback.getId());
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

    /**
     * Retrieve all records matching the given 'category' and 'tagFilter'. Unlike 
     * {@link Store#scanStart(java.lang.String, java.lang.String, java.lang.String, long, long, java.lang.String, boolean)}, 
     * this method may be used within a transaction. It should not be used for very large result sets due to correspondingly large memory requirements.
     * 
     * @param category
     * @param tagFilter
     * @param limit
     * @param orderBy
     * @param descending
     * @param forUpdate
     * @return
     * @throws AskarException 
     */
    public EntryList fetchAll(String category, String tagFilter, long limit, String orderBy, boolean descending, boolean forUpdate) throws AskarException {
        Objects.requireNonNull(handle, "Cannot fetch from a closed session.");
        Objects.requireNonNull(category);
        Objects.requireNonNull(tagFilter);
        Objects.requireNonNull(orderBy);
        AskarCallback.Pointer callback = new AskarCallback.Pointer();
        ErrorCode errorCode = AskarLibrary.askar_session_fetch_all(handle,
                category, tagFilter, limit, orderBy, (byte) (descending ? 1 : 0),
                (byte) (forUpdate ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Pointer p = callback.getPointer();
            if (p == Pointer.NULL) {
                return null;
            }
            EntryList list = new EntryList(p);
            return list;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
    
    /**
     * Retrieve all keys matching the given filters.
     * 
     * @param algorithm
     * @param thumbprint
     * @param tagFilter
     * @param limit
     * @param forUpdate
     * @return
     * @throws AskarException 
     */
    public KeyEntryList fetchAllKeys(KeyAlgorithm algorithm, String thumbprint, String tagFilter, long limit, boolean forUpdate) throws AskarException {
        Objects.requireNonNull(handle, "Cannot fetch from a closed session.");
        AskarCallback.Pointer callback = new AskarCallback.Pointer();
        ErrorCode errorCode = AskarLibrary.askar_session_fetch_all_keys(handle, algorithm.getAlgorithm(), thumbprint, tagFilter, limit, (byte)(forUpdate ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Pointer p = callback.getPointer();
            if (p == Pointer.NULL) {
                return null;
            }
            KeyEntryList list = new KeyEntryList(p);
            return list;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
    
    /**
     * Fetch an existing key from the store.
     * 
     * @param name
     * @param forUpdate Set to {@code true} when in a transaction to create an update lock on the associated record, if supported by the store backend.
     * @return
     * @throws AskarException 
     */
    public KeyEntry fetchKey(String name, boolean forUpdate) throws AskarException {
        Objects.requireNonNull(handle, "Cannot fetch from a closed session.");
        Objects.requireNonNull(name);
        AskarCallback.Pointer callback = new AskarCallback.Pointer();
        ErrorCode errorCode = AskarLibrary.askar_session_fetch_key(handle, name, (byte) (forUpdate ? 1 : 0), callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            Pointer p = callback.getPointer();
            if (p == Pointer.NULL) {
                return null;
            }
            KeyEntry entry = new KeyEntry(p, 0);
            return entry;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }

    /**
     * Insert a new record into the store.
     * 
     * @param category
     * @param name
     * @param tags
     * @param value
     * @param expiryMs
     * @throws AskarException 
     */
    public void insert(String category, String name, String tags, byte[] value, long expiryMs) throws AskarException {
        Objects.requireNonNull(handle, "Cannot insert with a closed session.");
        Objects.requireNonNull(category);
        Objects.requireNonNull(name);
        try (ByteBuffer.ByValue buffer = new ByteBuffer.ByValue(value)) {
            AskarCallback.Basic callback = new AskarCallback.Basic();
            ErrorCode errorCode = AskarLibrary.askar_session_update(handle,
                    EntryOperation.INSERT, category, name, buffer,
                    tags, expiryMs, callback, callback.getId());
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            try {
                callback.await();
            } catch (InterruptedException ex) {
                throw new AskarException(ex);
            }
        }
    }
    
    /**
     * Insert a local key instance into the store.
     * 
     * @param key
     * @param name
     * @param metadata
     * @param tags
     * @param expiryMs
     * @throws AskarException 
     */
    public void insertKey(Key key, String name, String metadata, String tags, long expiryMs) throws AskarException {
        Objects.requireNonNull(handle, "Cannot insert key with a closed session.");
        Objects.requireNonNull(name);
        Objects.requireNonNull(metadata);
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_session_insert_key(handle, key.handle, 
                name, metadata, tags, expiryMs, callback, callback.getId());
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
     * Remove a record from the store.
     * 
     * @param category
     * @param name
     * @throws AskarException 
     */
    public void remove(String category, String name) throws AskarException {
        Objects.requireNonNull(handle, "Cannot remove with a closed session.");
        Objects.requireNonNull(category);
        Objects.requireNonNull(name);

        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_session_update(handle,
                EntryOperation.REMOVE, category, name, null, null, 0,
                callback, callback.getId());
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
     * Remove all records in the store matching a given 'category' and 'tagFilter'.
     * @param category
     * @param tagFilter
     * @return
     * @throws AskarException 
     */
    public long removeAll(String category, String tagFilter) throws AskarException {
        Objects.requireNonNull(handle, "Cannot remove with a closed session.");
        Objects.requireNonNull(category);
        Objects.requireNonNull(tagFilter);
        AskarCallback.Long callback = new AskarCallback.Long();
        ErrorCode errorCode = AskarLibrary.askar_session_remove_all(handle, category, tagFilter, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
            long count = callback.getLong();
            return count;
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
    
    /**
     * Remove an existing key from the store.
     * @param name
     * @param expiryMs
     * @throws AskarException 
     */
    public void removeKey(String name, long expiryMs) throws AskarException {
        Objects.requireNonNull(handle, "Cannot remove with a closed session.");
        Objects.requireNonNull(name);
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_session_remove_key(handle, name, callback, callback.getId());
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
     * Replace the value and tags of a record in the store.
     * 
     * @param category
     * @param name
     * @param tags
     * @param value
     * @param expiryMs
     * @throws AskarException 
     */
    public void replace(String category, String name, String tags, byte[] value, long expiryMs) throws AskarException {
        Objects.requireNonNull(handle, "Cannot replace with a closed session.");
        Objects.requireNonNull(category);
        Objects.requireNonNull(name);
        try (ByteBuffer.ByValue buffer = new ByteBuffer.ByValue(value)) {
            AskarCallback.Basic callback = new AskarCallback.Basic();
            ErrorCode errorCode = AskarLibrary.askar_session_update(handle,
                    EntryOperation.REPLACE, category, name, buffer,
                    tags, expiryMs, callback, callback.getId());
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            try {
                callback.await();
            } catch (InterruptedException ex) {
                throw new AskarException(ex);
            }
        }
    }
    
    /**
     * Roll back the pending transaction.
     * 
     * @throws AskarException
     */
    public void rollback() throws AskarException {
        Objects.requireNonNull(handle, "Cannot rollback a closed session.");
        if (!isTransaction) {
            throw new IllegalStateException("Session is not a transation.");
        }
        close(false);
    }
    
    /**
     * Replace the metadata and tags on an existing key in the store.
     * 
     * @param name
     * @param metaData
     * @param tags
     * @param expiryMs
     * @throws AskarException
     */
    public void updateKey(String name, String metaData, String tags, long expiryMs) throws AskarException {
        Objects.requireNonNull(handle, "Cannot update with a closed session.");
        Objects.requireNonNull(name);
        Objects.requireNonNull(metaData);
        AskarCallback.Basic callback = new AskarCallback.Basic();
        ErrorCode errorCode = AskarLibrary.askar_session_update_key(handle, name, metaData, tags, expiryMs, callback, callback.getId());
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        try {
            callback.await();
        } catch (InterruptedException ex) {
            throw new AskarException(ex);
        }
    }
}
