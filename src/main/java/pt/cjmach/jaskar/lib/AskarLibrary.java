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

import com.sun.jna.Callback;
import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cmachado
 */
public class AskarLibrary {

    private static final String LIBRARY_NAME = "aries_askar";

    /**
     *
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    static {
        DefaultTypeMapper typeMapper = new DefaultTypeMapper();
        typeMapper.addTypeConverter(ErrorCode.class, new ErrorCode.Converter());

        Map<String, Object> options = new HashMap<>();
        options.put(Library.OPTION_TYPE_MAPPER, typeMapper);

        NativeLibrary nativeLib = NativeLibrary.getInstance(LIBRARY_NAME, options);
        Native.register(nativeLib);
    }

    /**
     *
     * @param buffer
     */
    public static native void askar_buffer_free(SecretBuffer.ByValue buffer);

    /**
     *
     */
    public static native void askar_clear_custom_logger();

    /**
     *
     * @param handle
     * @param count
     * @return
     */
    public static native ErrorCode askar_entry_list_count(Pointer handle, IntByReference count);

    /**
     *
     * @param handle
     */
    public static native void askar_entry_list_free(Pointer handle);

    /**
     *
     * @param handle
     * @param index
     * @param category
     * @return
     */
    public static native ErrorCode askar_entry_list_get_category(Pointer handle, int index, PointerByReference category);

    /**
     *
     * @param handle
     * @param index
     * @param name
     * @return
     */
    public static native ErrorCode askar_entry_list_get_name(Pointer handle, int index, PointerByReference name);

    /**
     *
     * @param handle
     * @param index
     * @param tags
     * @return
     */
    public static native ErrorCode askar_entry_list_get_tags(Pointer handle, int index, PointerByReference tags);

    /**
     *
     * @param handle
     * @param index
     * @param value
     * @return
     */
    public static native ErrorCode askar_entry_list_get_value(Pointer handle, int index, SecretBuffer value);

    /**
     *
     * @param error_json_p
     * @return
     */
    public static native ErrorCode askar_get_current_error(PointerByReference error_json_p);

    /**
     *
     * @param handle
     * @param ciphertext
     * @param nonce
     * @param tag
     * @param aad
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_aead_decrypt(Pointer handle,
            ByteBuffer.ByValue ciphertext, ByteBuffer.ByValue nonce,
            ByteBuffer.ByValue tag, ByteBuffer.ByValue aad, SecretBuffer out);

    /**
     *
     * @param handle
     * @param message
     * @param nonce
     * @param aad
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_aead_encrypt(Pointer handle,
            ByteBuffer.ByValue message, ByteBuffer.ByValue nonce,
            ByteBuffer.ByValue aad, EncryptedBuffer out);

    /**
     *
     * @param handle
     * @param msg_len
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_aead_get_padding(Pointer handle, long msg_len, IntByReference out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_aead_get_params(Pointer handle, AeadParams out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_aead_random_nonce(Pointer handle, SecretBuffer out);

    /**
     *
     * @param handle
     * @param alg
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_convert(Pointer handle, String alg, PointerByReference out);

    /**
     *
     * @param recip_key
     * @param sender_key
     * @param message
     * @param nonce
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_crypto_box(Pointer recip_key, Pointer sender_key,
            ByteBuffer.ByValue message, ByteBuffer.ByValue nonce, SecretBuffer out);

    /**
     *
     * @param recip_key
     * @param sender_key
     * @param message
     * @param nonce
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_crypto_box_open(Pointer recip_key, Pointer sender_key,
            ByteBuffer.ByValue message, ByteBuffer.ByValue nonce, SecretBuffer out);

    /**
     *
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_crypto_box_random_nonce(SecretBuffer out);

    /**
     *
     * @param handle
     * @param message
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_crypto_box_seal(Pointer handle,
            ByteBuffer.ByValue message, SecretBuffer out);

    /**
     *
     * @param handle
     * @param ciphertext
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_crypto_box_seal_open(Pointer handle,
            ByteBuffer.ByValue ciphertext,
            SecretBuffer out);

    /**
     *
     * @param alg
     * @param ephem_key
     * @param sender_key
     * @param recip_key
     * @param alg_id
     * @param apu
     * @param apv
     * @param cc_tag
     * @param receive
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_derive_ecdh_1pu(String alg,
            Pointer ephem_key, Pointer sender_key, Pointer recip_key, ByteBuffer.ByValue alg_id,
            ByteBuffer.ByValue apu, ByteBuffer.ByValue apv, ByteBuffer.ByValue cc_tag,
            byte receive, PointerByReference out);

    /**
     *
     * @param alg
     * @param ephem_key
     * @param recip_key
     * @param alg_id
     * @param apu
     * @param apv
     * @param receive
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_derive_ecdh_es(String alg,
            Pointer ephem_key, Pointer recip_key, ByteBuffer.ByValue alg_id,
            ByteBuffer.ByValue apu, ByteBuffer.ByValue apv, byte receive,
            PointerByReference out);

    /**
     *
     * @param handle
     * @param count
     * @return
     */
    public static native ErrorCode askar_key_entry_list_count(Pointer handle, IntByReference count);

    /**
     *
     * @param handle
     */
    public static native void askar_key_entry_list_free(Pointer handle);

    /**
     *
     * @param handle
     * @param index
     * @param alg
     * @return
     */
    public static native ErrorCode askar_key_entry_list_get_algorithm(Pointer handle,
            int index, PointerByReference alg);

    /**
     *
     * @param handle
     * @param index
     * @param metadata
     * @return
     */
    public static native ErrorCode askar_key_entry_list_get_metadata(Pointer handle,
            int index, PointerByReference metadata);

    /**
     *
     * @param handle
     * @param index
     * @param name
     * @return
     */
    public static native ErrorCode askar_key_entry_list_get_name(Pointer handle,
            int index, PointerByReference name);

    /**
     *
     * @param handle
     * @param index
     * @param tags
     * @return
     */
    public static native ErrorCode askar_key_entry_list_get_tags(Pointer handle,
            int index, PointerByReference tags);

    /**
     *
     * @param handle
     * @param index
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_entry_list_load_local(Pointer handle,
            int index, PointerByReference out);

    /**
     *
     * @param handle
     */
    public static native void askar_key_free(Pointer handle);

    /**
     *
     * @param jwk
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_from_jwk(ByteBuffer.ByValue jwk, PointerByReference out);

    /**
     *
     * @param alg
     * @param sk_handle
     * @param pk_handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_from_key_exchange(String alg,
            Pointer sk_handle, Pointer pk_handle, PointerByReference out);

    /**
     *
     * @param alg
     * @param public_
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_from_public_bytes(String alg, ByteBuffer.ByValue public_, PointerByReference out);

    /**
     *
     * @param alg
     * @param secret
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_from_secret_bytes(String alg, ByteBuffer.ByValue secret, PointerByReference out);

    /**
     *
     * @param alg
     * @param seed
     * @param method
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_from_seed(String alg, ByteBuffer.ByValue seed,
            String method, PointerByReference out);

    /**
     *
     * @param alg
     * @param key_backend
     * @param ephemeral
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_generate(String alg, String key_backend, byte ephemeral, PointerByReference out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_algorithm(Pointer handle, PointerByReference out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_ephemeral(Pointer handle, ByteByReference out);

    /**
     *
     * @param handle
     * @param alg
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_jwk_public(Pointer handle, String alg, PointerByReference out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_jwk_secret(Pointer handle, SecretBuffer out);

    /**
     *
     * @param handle
     * @param alg
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_jwk_thumbprint(Pointer handle, String alg, PointerByReference out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_public_bytes(Pointer handle, SecretBuffer out);

    /**
     *
     * @param handle
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_secret_bytes(Pointer handle, SecretBuffer out);

    /**
     *
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_get_supported_backends(PointerByReference out);

    /**
     *
     * @param handle
     * @param message
     * @param sig_type
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_sign_message(Pointer handle,
            ByteBuffer.ByValue message, String sig_type, SecretBuffer out);

    /**
     *
     * @param handle
     * @param alg
     * @param ciphertext
     * @param nonce
     * @param tag
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_unwrap_key(Pointer handle,
            String alg, ByteBuffer.ByValue ciphertext, ByteBuffer.ByValue nonce,
            ByteBuffer.ByValue tag, PointerByReference out);

    /**
     *
     * @param handle
     * @param message
     * @param signature
     * @param sig_type
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_verify_signature(Pointer handle,
            ByteBuffer.ByValue message, ByteBuffer.ByValue signature,
            String sig_type, ByteByReference out);

    /**
     *
     * @param handle
     * @param other
     * @param nonce
     * @param out
     * @return
     */
    public static native ErrorCode askar_key_wrap_key(Pointer handle,
            Pointer other, ByteBuffer.ByValue nonce, EncryptedBuffer out);

    /**
     * Migrate an sqlite wallet from an indy-sdk structure to an Askar
     * structure. It is important to note that this does not do any
     * post-processing. If the record values, tags, names, etc. have changed, it
     * must be processed manually afterwards. This script does the following:
     *
     * 1. Create and rename the required tables 2. Fetch the indy key from the
     * wallet 3. Create a new configuration 4. Initialize a profile 5. Update
     * the items from the indy-sdk 6. Clean up (drop tables and add a version of
     * "1")
     *
     * @param spec_uri
     * @param wallet_name
     * @param wallet_key
     * @param kdf_level
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_migrate_indy_sdk(String spec_uri,
            String wallet_name, String wallet_key, String kdf_level,
            BasicCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @return
     */
    public static native ErrorCode askar_scan_free(SizeT handle);

    /**
     *
     * @param handle
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_scan_next(SizeT handle, PointerCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param profile
     * @param category
     * @param tag_filter
     * @param offset
     * @param limit
     * @param order_by
     * @param descending
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_scan_start(SizeT handle, String profile,
            String category, String tag_filter, long offset, long limit,
            String order_by, byte descending, SizeTCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param commit
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_close(SizeT handle, byte commit,
            BasicCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param category
     * @param tag_filter
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_count(SizeT handle,
            String category, String tag_filter, LongCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param category
     * @param name
     * @param for_update
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_fetch(SizeT handle,
            String category, String name, byte for_update, PointerCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param category
     * @param tag_filter
     * @param limit
     * @param order_by
     * @param descending
     * @param for_update
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_fetch_all(SizeT handle,
            String category, String tag_filter, long limit, String order_by,
            byte descending, byte for_update, PointerCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param alg
     * @param thumbprint
     * @param tag_filter
     * @param limit
     * @param for_update
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_fetch_all_keys(SizeT handle,
            String alg, String thumbprint, String tag_filter, long limit,
            byte for_update, PointerCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param name
     * @param for_update
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_fetch_key(SizeT handle,
            String name, byte for_update, PointerCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param key_handle
     * @param name
     * @param metadata
     * @param tags
     * @param expiry_ms
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_insert_key(SizeT handle,
            Pointer key_handle, String name, String metadata, String tags,
            long expiry_ms, BasicCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param category
     * @param tag_filter
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_remove_all(SizeT handle,
            String category, String tag_filter, LongCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param name
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_remove_key(SizeT handle,
            String name, BasicCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param profile
     * @param as_transaction
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_start(SizeT handle,
            String profile, byte as_transaction, SizeTCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param operation
     * @param category
     * @param name
     * @param value
     * @param tags
     * @param expiry_ms
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_update(SizeT handle,
            byte operation, String category, String name, ByteBuffer.ByValue value,
            String tags, long expiry_ms, BasicCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param name
     * @param metadata
     * @param tags
     * @param expiry_ms
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_session_update_key(SizeT handle,
            String name, String metadata, String tags, long expiry_ms,
            BasicCallback cb, long cb_id);

//public static native ErrorCode askar_set_custom_logger(Pointer context,
//                                  LogCallback log,
//                                  struct Option_EnabledCallback enabled,
//                                  struct Option_FlushCallback flush,
//                                  int max_level);

    /**
     *
     * @return
     */
    public static native ErrorCode askar_set_default_logger();

    /**
     *
     * @param max_level
     * @return
     */
    public static native ErrorCode askar_set_max_log_level(int max_level);

    /**
     *
     * @param handle
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_close(SizeT handle, BasicCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param target_uri
     * @param key_method
     * @param pass_key
     * @param recreate
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_copy(SizeT handle,
            String target_uri, String key_method, String pass_key, byte recreate,
            SizeTCallback cb, long cb_id);

    /**
     *
     * @param from_handle
     * @param to_handle
     * @param from_profile
     * @param to_profile
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_copy_profile(SizeT from_handle,
            SizeT to_handle, String from_profile, String to_profile, BasicCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param profile
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_create_profile(SizeT handle,
            String profile, StringCallback cb, long cb_id);

    /**
     *
     * @param seed
     * @param out
     * @return
     */
    public static native ErrorCode askar_store_generate_raw_key(ByteBuffer.ByValue seed, PointerByReference out);

    /**
     *
     * @param handle
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_get_default_profile(SizeT handle,
            StringCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_get_profile_name(SizeT handle,
            StringCallback cb,
            long cb_id);

    /**
     *
     * @param handle
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_list_profiles(SizeT handle,
            PointerCallback cb, long cb_id);

    /**
     *
     * @param spec_uri
     * @param key_method
     * @param pass_key
     * @param profile
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_open(String spec_uri,
            String key_method, String pass_key, String profile, SizeTCallback cb,
            long cb_id);

    /**
     *
     * @param spec_uri
     * @param key_method
     * @param pass_key
     * @param profile
     * @param recreate
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_provision(String spec_uri,
            String key_method, String pass_key, String profile, byte recreate,
            SizeTCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param key_method
     * @param pass_key
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_rekey(SizeT handle,
            String key_method, String pass_key, BasicCallback cb, long cb_id);

    /**
     *
     * @param spec_uri
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_remove(String spec_uri,
            ByteCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param profile
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_remove_profile(SizeT handle,
            String profile, ByteCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param from_profile
     * @param to_profile
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_rename_profile(SizeT handle,
            String from_profile, String to_profile, ByteCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param profile
     * @param cb
     * @param cb_id
     * @return
     */
    public static native ErrorCode askar_store_set_default_profile(SizeT handle,
            String profile, BasicCallback cb, long cb_id);

    /**
     *
     * @param handle
     * @param count
     * @return
     */
    public static native ErrorCode askar_string_list_count(Pointer handle, IntByReference count);

    /**
     *
     * @param handle
     */
    public static native void askar_string_list_free(Pointer handle);

    /**
     *
     * @param handle
     * @param index
     * @param item
     * @return
     */
    public static native ErrorCode askar_string_list_get_item(Pointer handle, int index, PointerByReference item);

    /**
     *
     */
    public static native void askar_terminate();

    /**
     *
     * @return
     */
    public static native String askar_version();

    /* Callbacks */

    /**
     *
     */

    public static interface LogCallback extends Callback {

        /**
         *
         * @param context
         * @param level
         * @param target
         * @param message
         * @param module_path
         * @param file
         * @param line
         */
        void invoke(Pointer context, int level, String target, String message, String module_path, String file, int line);
    }

    /**
     *
     */
    public static interface BasicCallback extends Callback {

        /**
         *
         * @param cb_id
         * @param err
         */
        void invoke(long cb_id, ErrorCode err);
    }

    /**
     *
     */
    public static interface PointerCallback extends Callback {

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        void invoke(long cb_id, ErrorCode err, Pointer result);
    }

    /**
     *
     */
    public static interface SizeTCallback extends Callback {

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        void invoke(long cb_id, ErrorCode err, SizeT result);
    }

    /**
     *
     */
    public static interface LongCallback extends Callback {

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        void invoke(long cb_id, ErrorCode err, long result);
    }

    /**
     *
     */
    public static interface StringCallback extends Callback {

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        void invoke(long cb_id, ErrorCode err, String result);
    }

    /**
     *
     */
    public static interface ByteCallback extends Callback {

        /**
         *
         * @param cb_id
         * @param err
         * @param result
         */
        void invoke(long cb_id, ErrorCode err, byte result);
    }
}
