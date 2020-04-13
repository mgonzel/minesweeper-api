package constants.http

// HTTP Constants
// HTTP Status
const val RES_STATUS_OK = 200
const val RES_STATUS_CREATED = 201
const val RES_STATUS_NOT_FOUND = 404
const val RES_STATUS_BAD_REQUEST = 400
const val RES_STATUS_INTERNAL_ERROR = 500

// HTTP Headers
const val HEADER_NAME_CONTENT_TYPE = "Content-type"
const val HEADER_CONTENT_TYPE_APP_JSON = "application/json"

const val HEADER_NAME_CONTENT_LENGTH = "Content-Length"
const val HEADER_NAME_CACHE_CONTROL= "Cache-Control"
const val HEADER_NOT_FOUND_CACHE = "public, max-age=86400"