from scrapy import Spider
from scrapy.selector import Selector
import re

from courses.items import CoursesItem


class CoursesSpider(Spider):
    name = "courses"
    allowed_domains = ["tau.ac.il"]
    start_urls = [
        "http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=08&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=05&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=10&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=04&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=06&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=03&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=14&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=12&department9=&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=01&department10=&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=11&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=07&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=09&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=15&department11=&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=2171&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=2172&department12=&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=188018821883&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=1883&department13=",
        #"http://yedion.tau.ac.il/yed/yednew.dll?MfcISAPICommand=but&year=2016&semester=1&semester=2&semester=0&semester=3&department1=&department2=&department3=&department4=&department5=&department6=&department7=&course_nam=&teach_nam=&department8=&department9=&department10=&department11=&department12=&department13=1843",
    ]

    def parse(self, response):
        rows = Selector(response).xpath('//tr')
        count = 0
        num_of_days=0
        end_of_course = False
        for row in rows:
            if (count == 0):
                count=1
                continue #skip first line of table
            elif (count == 1):
                count=2
                item = CoursesItem()
                semester = ""
                hours = ""
                day = ""
                room = ""
                building = ""
                course_type = ""
                teacher = ""
                continue 
            elif (count == 2):
                ths = row.xpath('th')
                course_name = ths[0].xpath('div/text()').extract_first()
                course_name = course_name.replace("\n", "")
                item['course_name'] = course_name
                course_number = ths[1].xpath('text()').extract_first()
                item['course_number'] = course_number
                count=3
                continue
            elif (count == 3):
                faculty = ""
                school = ""
                school_and_faculty = row.xpath('th/text()').extract_first()
                school_and_faculty = school_and_faculty.replace("\n", "")
                if ("/" in school_and_faculty):
                    index = school_and_faculty.find("/")
                    faculty = school_and_faculty[:index]
                    school = school_and_faculty[index+1:]
                else:
                    faculty = school_and_faculty
                item['faculty'] = faculty
                item['school'] = school
                count=4
                continue
            elif (count == 4):
                count=5
                continue
            elif (count == 5):
                tds = row.xpath('td')
                if (len(tds)==7 or len(tds)==6):
                    maybe_new_line = ""
                    if (num_of_days != 0):
                        maybe_new_line = "\n"
                    semester = semester + maybe_new_line + str(tds[0].xpath('text()').extract_first())
                    item['semester'] = semester
                    hours = hours + maybe_new_line + str(tds[1].xpath('text()').extract_first())
                    item['hours'] = hours
                    day = day + maybe_new_line + str(tds[2].xpath('text()').extract_first())
                    item['day'] = day
                    room = room + maybe_new_line + str(tds[3].xpath('text()').extract_first())
                    item['room'] = room
                    building = building + maybe_new_line + str(tds[4].xpath('text()').extract_first())
                    item['building'] = building
                    course_type = course_type + maybe_new_line + str(tds[5].xpath('text()').extract_first())
                    item['course_type'] = course_type
                    if (len(tds)==7):
                        teacher = teacher + maybe_new_line + str(tds[6].xpath('text()').extract_first())
                        item['teacher'] = teacher
                    num_of_days = num_of_days + 1
                    continue
                elif (end_of_course):                
                    end_of_course = False
                    num_of_days = 0
                    count=1 #return to count=1 and not to count=0 because we skip the first line of the table
                    yield item
                    continue                   
                check_if_silabus_line = row.xpath('td[@class="borderbottomnew"]')
                if (len(check_if_silabus_line) > 0):
                    end_of_course = True
                    continue
