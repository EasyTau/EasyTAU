from scrapy import Spider
from scrapy.selector import Selector
import re

from tau_calendar.items import TauCalendarItem


class TauCalendarSpider(Spider):
    name = "tau_calendar"
    allowed_domains = ["tau.ac.il"]
    start_urls = [
        "https://www.tau.ac.il/calendar",
    ]

    def parse(self, response):
        rows = Selector(response).xpath('//div[@class="item"]//tr')
        for row in rows:
            item = TauCalendarItem()
            tds = row.xpath('td')
            event = ""
            ps = tds[0].xpath('p')
            for p in ps:
                event = event + str(p.xpath('descendant::text()').extract_first()) + " "
            if(len(ps) == 0):
                event = tds[0].xpath('descendant::text()').extract_first()
            index = event.find("(")
            if (index != -1):
                event = event[:index]
            item['event'] = event
            if (len(tds)==3):
                date = ""
                if (len(tds[1].xpath('p')) != 0):
                    ps = tds[1].xpath('p')
                    for p in ps:
                        date = date + str(p.xpath('descendant::text()').extract_first()) + " "                       
                else:
                    date = tds[1].xpath('descendant::text()').extract_first()
                item['date'] = date
                hebrew_date = ""
                if (len(tds[2].xpath('p')) != 0):
                    ps = tds[2].xpath('p')
                    for p in ps:
                        hebrew_date = hebrew_date + str(p.xpath('descendant::text()').extract_first()) + " " 
                else:
                    hebrew_date = tds[2].xpath('descendant::text()').extract_first()
                item['hebrew_date'] = hebrew_date
            yield item
