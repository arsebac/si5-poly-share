# SI5 Poly-share

## Team
- CANAVA Thomas aka Malandril
- GARDAIRE Loïc aka T0shiro
- JUNAC Jérémy aka Taken0711
- MELKONIAN François aka arsebac

## URL of our project

Home page :  
https://polyshare-cgjm.appspot.com/

Leaderboard page :  
https://polyshare-cgjm.appspot.com/users

## How to test our project

You can find all the POSTMAN requests you need to test our project in the file ``Polyshare - Test cases.postman_collection.json`` at the root of the project.  

When you import this file in POSTMAN, you will have the requests for testing.

First you have to set up the email address to receive the links. For that, you have to :
1. Click on the ... button below the ▶ button
1. Choose 🖊 _Edit_
1. Go to the _Variables_ tab
1. Change the ``email_address`` initial value with the content of your email address before the @

The folder ``Setup`` contains all the requests to create users of each level, and add a video to the database.
You can run it as a scenario.

⚠ For the rest of the requests, you have to run them one by one. There is a problem with the upload of files in the POSTMAN scenarios, so this requests cannot be included in a single scenario.
