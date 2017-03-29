from scrapy import Spider
from scrapy.selector import Selector
import re

from unitsphones.items import UnitsphonesItem


class UnitsphonesSpider(Spider):
    name = "unitsphones"
    allowed_domains = ["tau.ac.il"]
    start_urls = [
        "https://www.tau.ac.il/tau/index/units",
    ]

    def parse(self, response):
        categories = Selector(response).xpath(
            '//li[contains(@class, "units-category")]')

        for category in categories:
            category_id = re.findall(r'\d+', category.xpath('@class').extract()[0])[0]
            category_name = category.xpath('span/text()').extract()[0]

            xpath_str_catrgory_id = '//ul[contains(@class, "catid-'+category_id+'")]/li'
            units = Selector(response).xpath(xpath_str_catrgory_id)
            for unit in units:
                unit_id = re.findall(r'\d+', unit.xpath('@class').extract_first())[0]
                unit_name = unit.xpath('span/text()').extract_first()

                xpath_str_unit_id = '//tr[contains(@class, "unit-'+unit_id+'")]'
                #print("1")
                sub_units = Selector(response).xpath(xpath_str_unit_id)
                #print("2")
                for sub_unit in sub_units:
 #                   print("3")
                    item = UnitsphonesItem()
                    item['category'] = category_name
                    item['unit'] = unit_name
                    item['name'] = sub_unit.xpath(
                        'td[1]/text()').extract_first()
                    outer_phone = sub_unit.xpath(
                        'td[2]/text()').extract_first()
                    if ((outer_phone is not None) and (len(outer_phone) == 4)):
                        outer_phone = "03640" + outer_phone
                    item['outer_phone'] = outer_phone
                    inner_phone = sub_unit.xpath(
                        'td[3]/text()').extract_first()
                    if ((inner_phone is not None) and (len(inner_phone) == 4)):
                        inner_phone = "03640" + inner_phone
                    item['inner_phone'] = inner_phone
                    fax = sub_unit.xpath(
                        'td[4]/text()').extract_first()
                    if ((fax is not None) and (len(fax) == 4)):
                        fax = "03640" + fax
                    item['fax'] = fax
                    yield item

    
