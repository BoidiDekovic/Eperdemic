package ar.edu.unq.eperdemic.controller.dto

data class ErrorDTO (
    private val responseCode: Int,
    private val description: String) {

    override fun toString(): String {
        return "response_code: $responseCode \n" +
               "description: $description"
    }
}