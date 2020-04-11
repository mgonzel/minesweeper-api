package services

import com.mongodb.MongoClient
import org.bson.BsonString
import org.bson.Document

class MongoService (
        val databaseName : String,
        val collection : String = "default"
) {
    val mongoClient = MongoClient(config.MONGO_URI, config.MONGO_PORT.toInt())

    fun findOne (query: Map<String, String>) : Document? {
        val result = find(query)
        if (result.size > 1){
            throw Exception()
        } else if (result.size == 0){
            return null
        }
        return result.first()
    }

    private fun createQuery (query: Map<String, String>) : Document{
        val queryDoc = Document();
        query.forEach { k, v ->
            queryDoc.append(k, BsonString(v))
        }
        return queryDoc
    }
    private fun find (query: Map <String, String>) : List<Document?> {
        val queryDoc = createQuery(query)
        return mongoClient.getDatabase("users_db")
                .getCollection("users")
                .find(queryDoc).projection(Document("_id", 0)).toList()
    }

    fun save(toSaveData: Map<String, Any>) {
        mongoClient.getDatabase(databaseName)
                .getCollection(collection)
                .insertOne(Document(toSaveData))
    }
}