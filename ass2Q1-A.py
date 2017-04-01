from nltk.corpus import treebank
import numpy as np
from collections import Counter

corpus = treebank.tagged_sents()
#this list is used to store all the tags
tags = []
#this list is used to store all the (word,tag) pairs
observation = []
#this list is used to store all the words
words = []

for sent in corpus:
    sent.append(('</s>', 'END'))
    sent.insert(0,('<s>', 'START'))
    for word,tag in sent:
        tags.append(tag.lower())
        words.append(word.lower())
        observation.append((tag.lower(),word.lower()))

#this function is used to transfer the list into dictionary
def tagToDict(tags):
    Dict = {}
    for i in tags:
        Dict[i] = Dict.get(i,0) + 1
    return Dict

#this dictionary is used to store all tags and their counts
tags_counts = tagToDict(tags)
#this list is used to store Unique tags
tag_list = tags_counts.keys()
#this dictionary is used to store all words and their counts
words_counts = tagToDict(words)
#this list is used to store Unique words
words_list = words_counts.keys()

#the following two dicts are used for further (question 2 and 3) usages

#this dict is used to store each {(tag1,tag2),Prob(tag1 -> tag2)} pair
tag_tag_dict = {}
#this dict is used to store each {(tag,word),Prob(tag -> word)} pair
tag_word_dict = {}

#the matrix of A
A = np.zeros((len(tag_list),len(tag_list)))
#Here I use Counter to count each (tag,tag_next) pairs
countA = 0
for (x,y), count in Counter(zip(tags,tags[1:])).iteritems():
    prob = float(count)/tags_counts[x]
    A[tag_list.index(x),tag_list.index(y)] = prob
    tag_tag_dict[(x,y)] = prob
    countA += 1
