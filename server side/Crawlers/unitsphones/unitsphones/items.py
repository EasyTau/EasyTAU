# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field


class UnitsphonesItem(Item):
    category = Field()
    unit = Field()
    name = Field()
    outer_phone = Field()
    inner_phone = Field()
    fax = Field()
