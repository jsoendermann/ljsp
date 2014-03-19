/* This is a custom memory allocator that provides two functions:
 * jalloc: This function has the same functionality as malloc.
 * free_all: This function frees all memory that has been allocated with jalloc.
 */

#include <stdlib.h>

// Linked list that keeps pointers to all allocated memory blocks
typedef struct node {
    void *mem;
    struct node *next;
} node;

node *head = NULL;
node *tail = NULL;

void *jalloc(int bytes) {
    // Allocate memory block to be returned to the caller
    void *mem = malloc(bytes);

    // Allocate memory to hold new node and initialise
    node *block = (node*)malloc(sizeof(node));
    block->mem = mem;
    block->next = NULL;

    if (tail == NULL) {
        // Either first allocation overall, or first allocation after calling free_all()
        head = block;
        tail = block;
    } else {
        tail->next = block;
        tail = tail->next;
    }
    return block->mem;
}

void free_all() {
    node *cur = head, *next;
    while (cur != NULL) {
        next = cur->next;
        free(cur->mem);
        free(cur);
        cur = next;
    }

    head = tail = NULL;
}
