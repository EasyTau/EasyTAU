import pymongo

def name_without_spaces_in_begining_or_end(name):
    if (name == None):
        return name
    if (len(name)==0):
        return name
    while(name[0] == " "):
        name = name[1:]
        if (len(name)==0):
            return name
    while(name[len(name)-1] == " "):
        name = name[:len(name)-1]
        if (len(name)==0):
            return name
    return name

everything = []
connection = pymongo.MongoClient('mongodb://project:project@ds159737.mlab.com:59737/project')
db = connection['easytau']

##print("started getting data from courses")
##col = db["courses"]
##cursor = col.find({})
##for document in cursor:
##    item = {}
##    item["type"] = "courses"
##    item["course_number"] = name_without_spaces_in_begining_or_end(document["course_number"])
##    item["course_name"] = name_without_spaces_in_begining_or_end(document["course_name"])
##    item["day"] = name_without_spaces_in_begining_or_end(document["day"])
##    item["course_type"] = name_without_spaces_in_begining_or_end(document["course_type"])
##    item["faculty"] = name_without_spaces_in_begining_or_end(document["faculty"])
##    item["school"] = name_without_spaces_in_begining_or_end(document["school"])
##    item["teacher"] = name_without_spaces_in_begining_or_end(document["teacher"])
##    item["semester"] = name_without_spaces_in_begining_or_end(document["semester"])
##    item["building"] = name_without_spaces_in_begining_or_end(document["course_number"])
##    item["hours"] = name_without_spaces_in_begining_or_end(document["hours"])
##    item["room"] = name_without_spaces_in_begining_or_end(document["room"])
##    everything.append(item)
##print("finished getting data from courses")

##print("started getting data from courses_and_schools")
##col = db["courses_and_schools"]
##cursor = col.find({})
##for document in cursor:
##    item = {}
##    item["type"] = "courses_and_schools"
##    if ("course_name" in document):
##        item["course_name"] = name_without_spaces_in_begining_or_end(document["course_name"])
##    if ("school" in document):
##        item["school"] = name_without_spaces_in_begining_or_end(document["school"])
##    everything.append(item)
##print("finished getting data from courses_and_schools")
##
##print("started getting data from schools")
##col = db["schools"]
##cursor = col.find({})
##for document in cursor:
##    item = {}
##    item["type"] = "schools"
##    if ("school" in document):
##        item["school"] = name_without_spaces_in_begining_or_end(document["school"])
##    if ("faculty" in document):
##        item["faculty"] = name_without_spaces_in_begining_or_end(document["faculty"])
##    everything.append(item)
##print("finished getting data from schools")

print("started getting data from courses_numbers_schools_faculties")
col = db["courses_numbers_schools_faculties"]
cursor = col.find({})
for document in cursor:
    item = {}
    item["type"] = "courses_schools_faculties"
    if ("course_number" in document):
        item["course_number"] = document["course_number"]
    if ("school" in document):
        item["school"] = name_without_spaces_in_begining_or_end(document["school"])
    if ("faculty" in document):
        item["faculty"] = name_without_spaces_in_begining_or_end(document["faculty"])
    everything.append(item)
print("finished getting data from courses_numbers_schools_faculties")

print("started getting data from unitsphones")
col = db["unitsphones"]
cursor = col.find({})
for document in cursor:
    item = {}
    item["type"] = "unitsphones"
    if ("outer_phone" in document):
        item["outer_phone"] = name_without_spaces_in_begining_or_end(document["outer_phone"])
    if ("name" in document):
        item["name"] = name_without_spaces_in_begining_or_end(document["name"])
    if ("category" in document):
        item["category"] = name_without_spaces_in_begining_or_end(document["category"])
    if ("inner_phone" in document):
        item["inner_phone"] = name_without_spaces_in_begining_or_end(document["inner_phone"])
    if ("fax" in document):
        item["fax"] = name_without_spaces_in_begining_or_end(document["fax"])
    if ("unit" in document):
        item["unit"] = name_without_spaces_in_begining_or_end(document["unit"])
    everything.append(item)
print("finished getting data from unitsphones")

print("started getting data from tau_calendar")
col = db["tau_calendar"]
cursor = col.find({})
for document in cursor:
    item = {}
    item["type"] = "tau_calendar"
    if ("event" in document):
        item["event"] = name_without_spaces_in_begining_or_end(document["event"])
    if ("date" in document):
        item["date"] = name_without_spaces_in_begining_or_end(document["date"])
    if ("hebrew_date" in document):
        item["hebrew_date"] = name_without_spaces_in_begining_or_end(document["hebrew_date"])
    everything.append(item)
print("finished getting data from tau_calendar")

print("started saving data to everything")
col = db["everything6"]
for item in everything:
    col.insert(item)
print("finished saving data to everything")

