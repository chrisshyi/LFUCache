import java.util.HashMap;

class LFUCache {
    int capacity;
    FreqList freqList = new FreqList();
    HashMap<Integer, CacheItemNode> nodeMap = new HashMap<>();

    class FreqListNode {
        int frequency;
        CacheItemList cacheItemList;
        FreqListNode next;
        FreqListNode prev;

        public FreqListNode(int frequency) {
            this.frequency = frequency;
            cacheItemList = new CacheItemList();
        }
    }

    class FreqList {
        FreqListNode head;

        CacheItemNode increment(CacheItemNode node) {
            FreqListNode freqListNode = node.freqListNode;
            FreqListNode newFreqNode;
            if (freqListNode.next == null || freqListNode.next.frequency != freqListNode.frequency + 1) {
                FreqListNode oldNext = freqListNode.next;
                freqListNode.next = new FreqListNode(freqListNode.frequency + 1);
                freqListNode.next.prev = freqListNode;
                freqListNode.next.next = oldNext;
                if (oldNext != null) {
                    oldNext.prev = freqListNode.next;
                }
            }
            newFreqNode = freqListNode.next;
            CacheItemNode newCacheNode = new CacheItemNode(node.key, node.val);
            newCacheNode.freqListNode = newFreqNode;
            newFreqNode.cacheItemList.addFirst(newCacheNode);
            freqListNode.cacheItemList.removeNode(node);
            if (freqListNode.cacheItemList.isEmpty()) {
                removeNode(freqListNode);
            }
            return newCacheNode;
        }

        int evictLRU() {
            // Assumes head is not NULL
            // Returns the key of the removed node
            CacheItemNode removedNode = head.cacheItemList.removeLast();
            if (head.cacheItemList.isEmpty()) {
                removeNode(head);
            }
            return removedNode.key;
        }

        CacheItemNode addNewNode(int key, int val) {
            CacheItemNode newNode = new CacheItemNode(key, val);
            if (head == null || head.frequency != 1) {
                FreqListNode newFreqListNode = new FreqListNode(1);
                newFreqListNode.next = head;
                if (head != null) {
                    head.prev = newFreqListNode;
                }
                head = newFreqListNode;
            }
            head.cacheItemList.addFirst(newNode);
            newNode.freqListNode = head;
            return newNode;
        }

        void removeNode(FreqListNode node) {
            if (node == head) {
                head = head.next;
                if (head != null) {
                    head.prev = null;
                }
            } else {
                var prevNode = node.prev;
                var nextNode = node.next;
                if (nextNode != null) {
                    nextNode.prev = prevNode;
                }
                prevNode.next = nextNode;
            }
            node.prev = null;
            node.next = null;
        }
    }

    class CacheItemList {
        CacheItemNode head;
        CacheItemNode tail;

        boolean isEmpty() {
            return head == null;
        }

        CacheItemNode addFirst(CacheItemNode node) {
            if (head != null) {
                node.next = head;
                head.prev = node;
            }
            head = node;
            if (tail == null) {
                tail = node;
            }
            return node;
        }

        CacheItemNode removeFirst() {
            if (head == null)
                return null;
            if (head == tail) {
                CacheItemNode oldHead = head;
                head = null;
                tail = null;
                oldHead.next = null;
                oldHead.prev = null;
                return oldHead;
            }
            CacheItemNode oldHead = head;
            head = head.next;
            oldHead.next = null;
            if (head != null) {
                head.prev = null;
            }
            return oldHead;
        }

        CacheItemNode removeLast() {
            if (head == null) {
                return null;
            }
            if (head == tail) {
                var oldHead = head;
                head = null;
                tail = null;
                return oldHead;
            }
            var oldTail = tail;
            tail = tail.prev;
            tail.next = null;
            oldTail.prev = null;
            return oldTail;
        }

        void removeNode(CacheItemNode node) {
            if (node == null) {
                return;
            }
            if (node == head) {
                removeFirst();
                return;
            } else if (node == tail) {
                removeLast();
                return;
            } else {
                var prevNode = node.prev;
                var nextNode = node.next;
                if (nextNode == null) {
                    prevNode.next = null;
                } else {
                    prevNode.next = nextNode;
                    nextNode.prev = prevNode;
                }
            }
            node.next = null;
            node.prev = null;
        }
    }

    class CacheItemNode {
        FreqListNode freqListNode;
        int key;
        int val;
        CacheItemNode next;
        CacheItemNode prev;

        public CacheItemNode(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if (capacity == 0) {
            return -1;
        }
        if (!nodeMap.containsKey(key)) {
            return -1;
        }

        var newCacheItemNode = freqList.increment(nodeMap.get(key));
        nodeMap.put(key, newCacheItemNode);
        return newCacheItemNode.val;
    }

    public void put(int key, int value) {
        if (capacity == 0)
            return;
        if (nodeMap.containsKey(key)) {
            CacheItemNode node = nodeMap.get(key);
            node.val = value;
            var newCacheItemNode = freqList.increment(node);
            nodeMap.put(key, newCacheItemNode);
        } else {
            if (capacity == nodeMap.size()) {
                int keyOfEvicted = freqList.evictLRU();
                nodeMap.remove(keyOfEvicted);
            }
            CacheItemNode newNode = freqList.addNewNode(key, value);
            nodeMap.put(key, newNode);
        }
    }
}