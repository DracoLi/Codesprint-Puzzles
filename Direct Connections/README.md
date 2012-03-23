# Direct Connections (Codesprint: 35pt)
**[Source url](http://cs2.interviewstreet.com/recruit/challenges/solve/view/4f0a70674f380/4f000909cf26d)**

### Explanation:
This problem can be done in both a brute force method and a much faster method using Binary Indexed Trees.

**Brute Force: O(n^2)**
I simply looped through the cities, for each city I compared it with the rest of the cities and calculated the cables needed to connect each city with the cities after it. Distance in this case is simply the absolute difference between two cities and the population will be the population of the largest city. Since cities are compared to only the cities after it, the inner loop is executed n/2 times. Thus a more accurate complexity is O(n*n/2)

**Using Binary Indexed Trees**
After looking online I realized that this problem can be done using [Binary Indexed Trees (Fenwick Tree)](http://community.topcoder.com/tc?module=Static&d1=tutorials&d2=binaryIndexedTrees#read) since we are handling the cumulative distance between cities. I read a proposed solutions using BIT online ( [this](http://fusharblog.com/codesprint-2-interview-street-problems-analysis/)) but couldn't exactly understand his methodology. However the article gave me enough hints for me to derive a solution.
To do this problem I used two BITs, one that stores the cumulative cities of a cities array sorted by distance. This is used to keep track of how many valid cities are on the left and right of a city (cities become invalid after all cables have been constructed for that city).
For the second BIT, I used it for storing the cumulative distance of the distance-sorted cities array. This is used to calculate the cumulative distance of a city from the beginning and the end.
To calculate the total cables needed for a city I started calculating the cables needed by the largest population city since it will connect to every other city. Then all we need to do is figure out the index of that city in the distance sorted array and look at the total left distance and right distance. Next using the city count BIT we calculate the total cables needed by the city. Whenever a city is finished we then need to update that city's distance in our cumulative distance BIT to zero and the city's count to zero in the cities count BIT.
If you have any question regarding my solution please give me a email.
[draco@dracoli.com] 

Sources I looked into:
[FursharBlog](http://fusharblog.com/codesprint-2-interview-street-problems-analysis/)
[Binary Indexed Trees](http://community.topcoder.com/tc?module=Static&d1=tutorials&d2=binaryIndexedTrees#read)
[Blog on implementing Bit Indexed Tree](http://gborah.wordpress.com/2011/09/24/bit-indexed-tree-fenwick-tree/)
[Karthik's explaination of BIT](http://karthikpresumes.blogspot.ca/2011/01/binary-indexed-tree.html)