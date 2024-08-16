# ArtAuction

## Overview
Backend part for an auction app with the ability to add artwork, create auctions, and set notifications when your bid is outbid or when a desired artwork (either specific or filtered by criteria) is listed for auction.

## Implemented
- CREATE-READ operations for artwork, user and auction entities
- Auth for user entity
- Connection to MongoDB + create repository impl using MongoTemplate
- Unit test for service layer

## Key Features
**User Roles:**
- Artist: Organize auctions for artworks, post new artworks.
- Buyer: Buy artwork on auctions.

