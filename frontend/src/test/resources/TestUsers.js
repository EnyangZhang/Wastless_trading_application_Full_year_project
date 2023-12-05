const user = {
    "id": 100,
    "firstName": "John",
    "lastName": "Smith",
    "middleName": "Hector",
    "nickname": "Jonny",
    "bio": "Likes long walks on the beach",
    "email": "johnsmith99@gmail.com",
    "dateOfBirth": "1999-04-27",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": {
    "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "suburb": "Upper Riccarton",
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
    },
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
        {
            "id": 100,
            "administrators": [
                "string"
            ],
            "primaryAdministratorId": 20,
            "name": "Lumbridge General Store",
            "description": "A one-stop shop for all your adventuring needs",
            "address": {
                "streetNumber": "3/24",
                "streetName": "Ilam Road",
                "suburb": "Upper Riccarton",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            },
            "businessType": "Accommodation and Food Services",
            "created": "2020-07-14T14:52:00Z"
        }
    ]
}

export function getUser() {
    return user;
}