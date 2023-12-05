import {getKeywords} from "@/test/resources/TestKeywords";

const keywords = getKeywords();

const forSaleCards = [
    {
        "id": 500,
        "creator": {
            "id": 2,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
                "suburb": "Upper Riccarton",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            },
        },
        "section": "ForSale",
        "created": "2020-07-15T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "B Card",
        "description": "Coming soon...",
        "keywords": [
            keywords.vehicle,
            keywords.new
        ]
    },
    {
        "id": 501,
        "creator": {
            "id": 101,
            "firstName": "Hector",
            "lastName": "Yeo",
            "homeAddress": {
                "suburb": "Hornby",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "Hinterland",
                "postcode": "90210"
            },
        },
        "section": "ForSale",
        "created": "2021-07-15T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "A Card",
        "description": "Coming soon...",
        "keywords": [
            keywords.new
        ]
    },
    {
        "id": 502,
        "creator": {
            "id": 102,
            "firstName": "Hector",
            "lastName": "Yeo",
            "homeAddress": {
                "suburb": "A",
                "city": "Z",
                "region": "Canterbury",
                "country": "B Country",
                "postcode": "90210"
            },
        },
        "section": "ForSale",
        "created": "2019-07-15T05:10:00Z",
        "displayPeriodEnd": "202-07-29T05:10:00Z",
        "title": "@ Card",
        "description": "Coming soon...",
        "keywords": [
            keywords.vehicle,
            keywords.new,
            keywords.other
        ]
    },
    {
        "id": 503,
        "creator": {
            "id": 103,
            "firstName": "Hector",
            "lastName": "Yeo",
            "homeAddress": {
                "suburb": "C Suburb",
                "city": "Z City",
                "region": "Canterbury",
                "country": "D Country",
                "postcode": "90210"
            },
        },
        "section": "ForSale",
        "created": "2019-06-15T05:10:00Z",
        "displayPeriodEnd": "202-07-29T05:10:00Z",
        "title": "123 Card",
        "description": "Coming soon...",
        "keywords": [
           keywords.vehicle
        ]
    }
];

const exchangeCards = [
    {
        "id": 500,
        "creator": {
            "id": 100,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
                "suburb": "Upper Riccarton",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            },
        },
        "section": "ForSale",
        "created": "2021-07-15T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "Exchange Card" + "A".repeat(100),
        "description": "A".repeat(5000),
        "keywords": [
            keywords.vehicle
        ]
    }
];

const wantedCards = [
    {
        "id": 500,
        "creator": {
            "id": 100,
            "firstName": "John",
            "lastName": "Smith",
            "homeAddress": {
                "suburb": "Upper Riccarton",
                "city": "Christchurch",
                "region": "Canterbury",
                "country": "New Zealand",
                "postcode": "90210"
            },
        },
        "section": "ForSale",
        "created": "2021-07-15T05:10:00Z",
        "displayPeriodEnd": "2021-07-29T05:10:00Z",
        "title": "Wanted Card AAAAAAAAAAAAAAAAAAAAAAAAAA",
        "description": 'A'.repeat(500),
        "keywords": [
        ]
    }
];

const card = {
    "id": 500,
    "creator": {
        "id": 100,
        "firstName": "John",
        "lastName": "Smith",
        "homeAddress": {
            "suburb": "Upper Riccarton",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand",
            "postcode": "90210"
        },
    },
    "section": "ForSale",
    "created": "2021-07-15T05:10:00Z",
    "displayPeriodEnd": "2021-07-29T05:10:00Z",
    "title": "Wanted Card",
    "description": "A".repeat(200),
    "keywords": [
    ]
}


export function getCards() {
    return {
        forSaleCards,
        exchangeCards,
        wantedCards
    };
}

export function getNCards(n) {
    let cards = [];
    for (let i = 0; i < n; i++) {
        cards.push(card);
    }
    return cards;
}