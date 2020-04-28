# LFUCache
A simple cache implementing the least-frequently-used (LFU) eviction policy

The implementation uses a hash table with a nested linked list to achieve O(1) get and O(1) set.

The algorithm is from [Shah, Mitra, and Matani (2010)](http://dhruvbird.com/lfu.pdf), and referenced in [Ilija Eftimov's blog](https://ieftimov.com/post/when-why-least-frequently-used-cache-implementation-golang/)

