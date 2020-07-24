const mapping = {
    "mappings": {
        "properties": {
            "userid": {
                "type": "integer"
            },
            "username": {
                "type": "text",
                "analyzer": "username_index_ngram",
                "search_analyzer": "username_search_ngram"
            },
            "displayName": {
                "type": "text",
                "analyzer": "standard",
                "fields": {
                    "ngram": {
                        "type": "text",
                        "analyzer": "displayname_index_ngram",
                        "search_analyzer": "displayname_search_ngram"
                    },
                    "folding": {
                        "type": "text",
                        "analyzer": "lowercase_asciifolding_ngram",
                        "search_analyzer": "displayname_search_ngram"
                    }
                }
            },
            "createdAt": {
                "type": "long"
            },
            "gender": {
                "type": "integer"
            },
            "phone": {
                "type": "keyword"
            },
            "dob": {
                "type": "long"
            },
            "avatar": {
                "type": "keyword"
            },
            "lastLocation": {
                "type": "geo_point"
            },
            "updatedAt": {
                "type": "long"
            }
        }
    },
    "settings": {
        "index": {
            "analysis": {
                "analyzer": {
                    "displayname_index_ngram": {
                        "tokenizer": "displayname_ngram_tokenizer",
                        "filter": ["lowercase"]
                    },
                    "displayname_search_ngram": {
                        "tokenizer": "keyword",
                        "filter": ["lowercase"]
                    },
                    "lowercase_asciifolding_ngram": {
                        "tokenizer": "displayname_ngram_tokenizer",
                        "filter": ["lowercase", "asciifolding"]
                    },
                    "username_index_ngram": {
                        "tokenizer": "username_ngram_tokenizer",
                        "filter": ["lowercase"]
                    },
                    "username_search_ngram": {
                        "tokenizer": "standard",
                        "filter": ["lowercase"]
                    }
                },
                "tokenizer": {
                    "displayname_ngram_tokenizer": {
                        "type": "ngram",
                        "min_gram": 2,
                        "max_gram": 30
                    },
                    "username_ngram_tokenizer": {
                        "type": "ngram",
                        "min_gram": 2,
                        "max_gram": 20
                    }
                }
            },
            "max_ngram_diff": 30
        }
    }
}

module.exports = mapping;