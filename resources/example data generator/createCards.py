import datetime
import random


NUMBER_OF_CARDS = 500
CURRENTCARDID = 1


cardAdjectivesFile = open("resources/cardAdjectives.txt", "r")
productNamesFile = open("resources/productNames.txt", "r")

cardAdjectives = cardAdjectivesFile.readlines()
productNames = productNamesFile.readlines()


class MarketplaceCard:
    """ MarketplaceCard class.
        Called to create an example market place card for the system using random data.
    """

    # Class initializer calls methods to set attributes of a product object
    def __init__(self):
        self.section = self.get_section()
        self.title = self.get_title()
        self.description = self.get_description()

    @staticmethod
    def get_section():
        return random.choice(['For Sale', 'Wanted', 'Exchange'])

    # Chooses a random product name from the product names file (works for card title too)
    @staticmethod
    def get_title():
        return random.choice(productNames).strip()

    # Generates the description for a product using a randomly chosen adjective and the product name
    def get_description(self):
        verb = None
        if self.section == 'For Sale':
            amount = random.randint(0, 100)
            verb = f"Will sell for ${amount}."
        elif self.section == "Wanted":
            verb = f"Wanted for lesson than ${random.randint(0, 100)}."
        else:
            verb = f"Will exchange for {random.choice(productNames).strip()}."
        return random.choice(cardAdjectives).strip() + " " + self.title + ". " + verb


def create_card_sql(card_list, card_id, user_id):
    card_query = 'INSERT INTO marketplace_card (MARKETPLACE_CARD_ID, CREATOR_ID, SECTION, TITLE, DESCRIPTION, CREATED, DISPLAY_PERIOD_END) VALUES '
    for card in card_list:
        now = datetime.date.today()
        subtract_days = random.randint(0, 14)
        now = now + datetime.timedelta(0 - subtract_days)
        closing = datetime.date.today()
        closing = closing + datetime.timedelta(21 - subtract_days)
        card_query += "({}, {}, '{}', '{}', '{}', '{}', '{}'), ".format(card_id,
                                                                            user_id,
                                                                            card.section,
                                                                            card.title,
                                                                            card.description,
                                                                            str(now),
                                                                            str(closing))
        card_id += 1
        user_id += 1
    return card_query[:-2] + ';'


# creates list of cards
cards = []
for i in range(NUMBER_OF_CARDS):
    cards.append(MarketplaceCard())

cardResult = create_card_sql(cards, CURRENTCARDID, 10)

cardFile = open('scripts/marketplaceCards.sql', 'w')
cardFile.writelines(cardResult)
