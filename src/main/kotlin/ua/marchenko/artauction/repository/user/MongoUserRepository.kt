package ua.marchenko.artauction.repository.user

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.model.User

@Repository
interface MongoUserRepository: MongoRepository<User, String> {

}