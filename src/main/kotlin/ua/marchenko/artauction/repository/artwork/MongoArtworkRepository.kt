package ua.marchenko.artauction.repository.artwork

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ua.marchenko.artauction.model.Artwork

@Repository
interface MongoArtworkRepository: MongoRepository<Artwork, String>{

}