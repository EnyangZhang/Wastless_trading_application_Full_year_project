Seng302 team 600 project
=========

project using `gradle`, `npm`, `Spring Boot`, `Vue.js` and `Gitlab CI`.

## Synopsis
“Every year, New Zealanders send around 2.5 million tonnes of waste to landfill” - over a tonne of rubbish per 
household. https://www.mfe.govt.nz/waste/waste-guidance-and-technical-information

Wasteless is a software application where stores can advertise any products that they are about to throw out at a reduced cost to the public. The primary purpose of this application is to reduce the amount of wastage.
by allowing people to buy and sell food and other goods which are close to expiry.

Two types of users will use this application: businesses and individuals. Businesses are administered by 
individuals. We currently allow businesses and individuals to buy and sell products (we may place restrictions later). 

For help with Wasteless, refer to our user guide: https://docs.google.com/document/d/1r2JWKFFas7xBX2KsikQlOZHbzT_erhCBSEO96iR1mkQ/view

## Basic Project Structure

A frontend sub-project (web GUI):

- `frontend/src` Frontend source code (Vue.js)
- `frontend/public` publicly accessible web assets (e.g., icons, images, style sheets)
- `frontend/dist` Frontend production build

A backend sub-project (business logic and persistence server):

- `backend/src` Backend source code (Java - Spring)
- `backend/out` Backend production build

## How to run

### Pre-population

    Scripts are found in root/resources/example data generator / scripts
    Order to run the scripts:
        - userAddress
        - userAccounts
        - businessAddress
        - businessAccounts
        - businessAdmins
        - adminAddress
        - adminAccounts

    The scripts can be run through the database's console (H2 and PHP). 
    PHP allows importing them as files (import -> choose file -> go), drag and drop anywhere onto the console client 
    executes them automatically

    Note: The PHP database's table names are case sensitive. Make sure table names (e.g. INSERT INTO [table]) match
    the PHP database table names.

### Frontend / GUI

    $ cd frontend
    $ npm install
    $ npm run serve

Running on: http://localhost:9500/ by default

### Backend / server

    cd backend
    ./gradlew bootRun

Running on: http://localhost:9499/ by default

### Deploy in production

An automated-deploy script is active on Gitlab. To deploy the latest version of the product, merge the `dev`
branch into the `master` branch. The latest deployed site will then be accessible at
https://csse-s302g6.canterbury.ac.nz/prod/

## Contributors

- SENG 302 Teaching Team
- Enyang Zhang (ezh15)
- Raven Townsend (rto45)
- George Stephenson (gms122)
- Michael Peters (mwp42)
- Harry Seigne (hgs30)
- Bryson Chen (bjc199)
- Fa Wren Chong (fwc14)
- George Holden (gah83)

## DGAA Credentials (For assessment purposes only)
- Username: admin@defaultglobal
- Password: 1tsOnTheW1K1

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue docs](https://vuejs.org/v2/guide/)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=10577&section=11)
