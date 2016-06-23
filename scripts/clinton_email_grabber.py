#!/usr/bin/python

import requests
import sys
import json
import csv


def clean_request(req):
    return ",".join([ line for line in req.split(',') if not(line.strip().startswith('\"docDate\"') or line.strip().startswith('\"postedDate\"'))])

def total_num_pages(total_num_docs=30322, num_returned=50):
    return 30322/50

def get_url(page_number, total_num_docs=30322, num_returned=50):
    start = num_returned*page_number
    url = 'https://foia.state.gov/searchapp/Search/SubmitSimpleQuery?_dc=1466446302048&searchText=*&beginDate=false&endDate=false&collectionMatch=Clinton_Email&postedBeginDate=false&postedEndDate=false&caseNumber=false&page=' + str(page_number+1) + '&start=' + str(start) + '&limit=' + str(num_returned)
    r = requests.get(url)
    req_text = r.text
    req_text_stripped = req_text.encode('ascii', 'ignore').decode('ascii')
    return json.loads(clean_request(req_text_stripped))['Results']

def main():
    with open('metadata.csv', 'wb') as csvfile:
        out = csv.writer(csvfile, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        for i in xrange(0, total_num_pages()):
            for doc in get_url(i):
                out.writerow([ doc['from'], 'https://foia.state.gov/searchapp/' + doc['pdfLink'] ])

if __name__ == '__main__':
    main()
