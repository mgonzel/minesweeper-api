package services

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import java.lang.Exception



public class VaultService {

    companion object VaultConfiguration {

        private val logger = mu.KotlinLogging.logger {}

        val vaultConfig = mutableMapOf<String, String>()

        fun getValue(key: String): String? {
            return vaultConfig.get(key)
        }

        fun loadVaultConfig() {
            if (config.VAULT_URL == config.NO_VALUE || config.VAULT_TOKEN_VALUE == config.NO_VALUE){
                logger.warn ("No Vault configuration can be loaded")
                return
            }
            try {
                val jsonResponse: HttpResponse<JsonNode> = Unirest.get(config.VAULT_URL)
                        .header(config.VAULT_TOKEN_HEADER, config.VAULT_TOKEN_VALUE)
                        .asJson();
                val json = jsonResponse.body
                val envVars = json.getObject().getJSONObject("data").getJSONObject("data")

                logger.info("Variables from Vault:")
                for (key in envVars.keySet()) {
                    logger.info("$key:************************")
                    vaultConfig.set(key, envVars.get(key).toString())
                }

                logger.info("Variables from Vault loaded.")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}