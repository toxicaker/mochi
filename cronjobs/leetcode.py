import requests
import json
from pymongo import MongoClient


def connect_mongo(host, port):
    client = MongoClient(host, port)
    db = client['mochi']
    return db


db = connect_mongo('localhost', 27017)


def login(username, password):
    """
    todo: solve the blocker of recaptcha_token
    login LeetCode
    :param username:
    :param password:
    :return: csrftoken and session id
    """

    def get_token(cookie):
        csrftoken = ''

        for msg in cookie.split(' '):
            if msg.startswith('csrftoken'):
                csrftoken = msg.split('=')[1].strip(';')
        return csrftoken

    res = requests.get("https://leetcode.com")
    cookie = res.headers['Set-Cookie']

    token = get_token(cookie)

    data = {'login': username, 'password': password,
            'csrfmiddlewaretoken': token,
            'recaptcha_token': '03AHaCkAZcmCFzRcOqXHBfAt3NdT7kwGiNIRDwyYgec-15v5HYTbfSoxh7hwunSf50sJms5WyRIsa6MzF8_np-nRje2faFVxo5l4z_tZJN_UCpGRIXWtYNPPwQAJHS68CpxxpPiyfI-9Xej200uRCcKHNoJwa93a0HdjMBE507QKY5LjjDLnZn4Nkn3C3bOHbgWhnYeUEBp48_W0atdVafVslldQZKvzMd5Eo5QwGW2tVylGY8DwyNjoENjoxujZeokewnyRpQdDtEXY85hvHQNKENrhTBoL_88a7orJ4l5sTqwdS7tZQG9PyiXu5M4U9CJo2Om8pNtSx8Orf1XwN-9dCfgZLIAueYv23KZKLbKhGXszG1Vhlmr1TlZCaKy9qc7A6CH5u5pNGs'}

    headers = {'authority': 'leetcode.com',
               'scheme': 'https',
               'origin': 'https://leetcode.com',
               'referer': 'https://leetcode.com/accounts/login/',
               'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) '
                             'Chrome/67.0.3396.87 Safari/537.36',
               'x-csrftoken': token,
               'sec-fetch-dest': 'empty',
               'sec-fetch-mode': 'cors',
               'sec-fetch-site': 'same-origin',
               'x-requested-with': 'XMLHttpRequest'}

    cookies = {'csrftoken': token}

    resp = requests.post("https://leetcode.com/accounts/login/", data=data, headers=headers, cookies=cookies)

    if resp.status_code != 200:
        print(resp.text)
        exit(1)

    resp_cookies = resp.cookies

    return resp_cookies['csrftoken'], resp_cookies['LEETCODE_SESSION']


def get_algorithm_info(token, session):
    def get_algorithm_detail(token, leetcode_session, title_slag):

        headers = {'referer': 'https://leetcode.com/problems/' + title_slag + '/description/',
                   'origin': 'https://leetcode.com/',
                   'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) '
                                 'Chrome/67.0.3396.87 Safari/537.36',
                   'x-csrftoken': token}

        cookies = {'csrftoken': token,
                   'LEETCODE_SESSION': leetcode_session}

        data = {"operationName": "questionData", "variables": {"titleSlug": title_slag},
                "query": "query questionData($titleSlug: String!) {\n" +
                         " question(titleSlug: $titleSlug) {\n" +
                         "    questionId\n" +
                         "    questionFrontendId\n" +
                         "    boundTopicId\n" +
                         "    title\n" +
                         "    titleSlug\n" +
                         "    content\n" +
                         "    translatedTitle\n" +
                         "    translatedContent\n" +
                         "    isPaidOnly\n" +
                         "    difficulty\n" +
                         "    likes\n" +
                         "    dislikes\n" +
                         "    isLiked\n" +
                         "    similarQuestions\n" +
                         "    contributors {\n" +
                         "      username\n" +
                         "      profileUrl\n" +
                         "      avatarUrl\n" +
                         "      __typename\n" +
                         "    }\n" +
                         "    langToValidPlayground\n" +
                         "    topicTags {\n" +
                         "      name\n" +
                         "      slug\n" +
                         "      translatedName\n" +
                         "      __typename\n" +
                         "    }\n" +
                         "    companyTagStats\n" +
                         "    codeSnippets {\n" +
                         "      lang\n" +
                         "      langSlug\n" +
                         "      code\n" +
                         "      __typename\n" +
                         "    }\n" +
                         "    stats\n" +
                         "    hints\n" +
                         "    solution {\n" +
                         "      id\n" +
                         "      canSeeDetail\n" +
                         "      paidOnly\n" +
                         "      __typename\n" +
                         "    }\n" +
                         "    status\n" +
                         "    sampleTestCase\n" +
                         "    metaData\n" +
                         "    judgerAvailable\n" +
                         "    judgeType\n" +
                         "    mysqlSchemas\n" +
                         "    enableRunCode\n" +
                         "    enableTestMode\n" +
                         "    enableDebugger\n" +
                         "    envInfo\n" +
                         "    libraryUrl\n" +
                         "    adminUrl\n" +
                         "    __typename\n" +
                         "  }\n" +
                         "}"}

        res = requests.post("https://leetcode.com/graphql", headers=headers, json=data, cookies=cookies)
        desc = None
        lid = None
        json_map = json.loads(res.text)

        try:
            lid = json_map['data']['question']['questionFrontendId']
            desc = json_map['data']['question']['content']
        except Exception as e:
            print('Failed to get question detail: ', e)

        return lid, desc

    headers = {'referer': 'https://leetcode.com/',
               'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) '
                             'Chrome/67.0.3396.87 Safari/537.36', 'x-csrftoken': token}

    cookies = {'csrftoken': token, 'LEETCODE_SESSION': session}

    res = requests.get("https://leetcode.com/api/problems/all/", headers=headers, cookies=cookies)

    json_map = json.loads(res.text)

    problems = json_map['stat_status_pairs']

    for p_info in problems:
        problem = {}
        try:
            problem['title'] = p_info['stat']['question__title']
            problem['titleSlug'] = p_info['stat']['question__title_slug']
            problem['leetCodeId'] = p_info['stat']['question_id']
            problem['type'] = p_info['paid_only']
            problem['frequency'] = p_info['frequency']
            problem['difficulty'] = p_info['difficulty']['level']
            problem['acceptance'] = p_info['stat']['total_acs'] / float(p_info['stat']['total_submitted'])

            lid, desc = get_algorithm_detail(token, session, p_info['stat']['question__title_slug'])
            problem['problemNum'] = int(lid)
            problem['content'] = desc
            exist = db.leetcode_problems.find_one({'leetCodeId': p_info['stat']['question_id']})
            if not exist:
                db.leetcode_problems.insert(problem)
                print('Successfully saved the question: ' + str(problem['problemNum']) + ' ' + problem['title'])
            else:
                print('Skipped the question: ' + str(problem['problemNum']) + ' ' + problem['title'])
        except Exception as e:
            print('Failed to get algorithm question: ', e)


def get_tag_info(token, session):
    """
    get information of different topics (array, dfs, bfs......)
    :param token:
    :param session:
    :return:
    """
    headers = {'referer': 'https://leetcode.com/',
               'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) '
                             'Chrome/67.0.3396.87 Safari/537.36', 'x-csrftoken': token}

    cookies = {'csrftoken': token, 'LEETCODE_SESSION': session}

    res = requests.get("https://leetcode.com/problems/api/tags/", headers=headers, cookies=cookies)
    json_map = json.loads(res.text)
    topics = json_map['topics']
    for topic in topics:
        try:
            print(topic['name'], topic['slug'], topic['questions'])
        except Exception as e:
            print('Failed to get tags info: ', e)


get_algorithm_info('vtwgtUWVl9VCiDcQI0zmYtSiIdMRh8uKgA6lhuyFwnuXl8i2uQIc5WiIDLutbxsE',
                   'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJfYXV0aF91c2VyX2lkIjoiNzYwODQ3IiwiX2F1dGhfdXNlcl9iYWNrZW5kIjoiYWxsYXV0aC5hY2NvdW50LmF1dGhfYmFja2VuZHMuQXV0aGVudGljYXRpb25CYWNrZW5kIiwiX2F1dGhfdXNlcl9oYXNoIjoiMWY4YTdjMTM0N2U3MGFkNTdmNjJhMjYzMzg0NmU3YWU5NTBkNDc4MyIsImlkIjo3NjA4NDcsImVtYWlsIjoiamlhdGVuZy5saWFuZ0BueXUuZWR1IiwidXNlcm5hbWUiOiJUb3hpY2FrZXIiLCJ1c2VyX3NsdWciOiJ0b3hpY2FrZXIiLCJhdmF0YXIiOiJodHRwczovL3d3dy5ncmF2YXRhci5jb20vYXZhdGFyL2VlYTg5NzcyMTEwM2M3M2YyNzFmMjQ5OTM5MTYxNmRiLnBuZz9zPTIwMCIsInRpbWVzdGFtcCI6IjIwMjAtMDQtMDIgMDM6MjM6NTMuNzY1NTMwKzAwOjAwIiwiSVAiOiI5OS4xMDUuMjEzLjgwIiwiSURFTlRJVFkiOiI2OTZlN2Y0NmQ1ODExMTQzNWM5MTlhNzIzNzViZTVhZSIsIl9zZXNzaW9uX2V4cGlyeSI6MTIwOTYwMH0.d-U_rCtrKyUWAadJ5K7DuH9MX4GOTAz2ZEQ4jv2h3R0')
