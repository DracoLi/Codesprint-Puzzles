# Coin Toss (Codesprint: 20pt)
**[Source url](http://cs2.interviewstreet.com/recruit/challenges/solve/view/4f0a70674f380/4eff8af9879d1)**

### Explanation:
In understanding this question we first need to determine the tosses needed to get N consecutive heads from the start (M = 0).
This [excellent paper from Cornell](http://people.ccmr.cornell.edu/~ginsparg/INFO295/mh.pdf) proves that the E(N, 0) = 2^(N+1) - 2.
From then on to calculate the probably for getting N consecutive heads when M > 0 we just needed to subtract the number of tosses needed to get M consecutive heads. 
**Thus E(N, M) = 2^(N+1) - 2 - (2^(M+1) - 2)  = 2^(N+1) - 2^(M+1)**

Got help from the following sources in understanding how to do this problem:
- [A blog](http://aakash0104.blogspot.ca/2012/01/codesprint-2-coin-tosses.html)
- [Stack Exchange](http://stats.stackexchange.com/a/23003)
- [Programming Logic](http://www.programminglogic.com/codesprint-2-problem-how-many-coin-tosses-to-get-n-heads/)
- [Cornell Paper](http://people.ccmr.cornell.edu/~ginsparg/INFO295/mh.pdf)