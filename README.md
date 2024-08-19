# ArtAuction

## Overview
Backend part for an auction app with the ability to add artwork, create auctions, and set notifications when your bid is outbid or when a desired artwork (either specific or filtered by criteria) is listed for auction. 
So firstly artist could add new artwork to the platform and then (whenever he/she wants) create an auction for this artwork. User with role buyer could view the artworks, auction, place a bid or set notifications 

## Main logic
- Add artwork: user with role ARTIST add new artwork => artistId is set from security context, artwork status is set to VIEW
- Create auction: user with role ARTIST create auction => artwork status is checked to be VIEW and changed to ON_AUCTION
- Registration: user service check if user with given email exist

## Implemented
- CREATE-READ operations for artwork, user and auction entities
- Auth for user entity
- Connection to MongoDB + create repository impl using MongoTemplate
- Unit test for controllers, services, mappers
- General utils for tests (getRandomUser etc)

## Key Features
**User Roles:**
- Artist: Organize auctions for artworks, post new artworks.
- Buyer: Buy artwork on auctions.
