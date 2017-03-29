# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field


class CoursesItem(Item):
    course_number = Field()
    course_name = Field()
    faculty = Field()
    school = Field()
    teacher = Field()
    course_type = Field()
    building = Field()
    room = Field()
    day = Field()
    hours = Field()
    semester = Field()



