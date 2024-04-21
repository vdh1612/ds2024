#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

#define MAX_PATH_LENGTH 256
#define MAX_THREADS 4

char longest_path[MAX_PATH_LENGTH] = ""; // Store the longest path found
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

// Map function
void* map(void* arg) {
    FILE* file = fopen("paths.txt", "r");
    if (file == NULL) {
        perror("Error opening file");
        exit(EXIT_FAILURE);
    }

    char path[MAX_PATH_LENGTH];

    while (fgets(path, MAX_PATH_LENGTH, file) != NULL) {
        int length = strlen(path);
        // Remove newline character if present
        if (path[length - 1] == '\n')
            path[length - 1] = '\0';

        // Check if path length is greater than current longest path length
        pthread_mutex_lock(&mutex);
        if (length > strlen(longest_path)) {
            strcpy(longest_path, path);
        }
        pthread_mutex_unlock(&mutex);
    }

    fclose(file);
    return NULL;
}

// Reduce function
void reduce() {
    printf("Longest path found: %s\n", longest_path);
}

int main() {
    pthread_t threads[MAX_THREADS];

    for (int i = 0; i < MAX_THREADS; i++) {
        pthread_create(&threads[i], NULL, map, NULL);
    }

    for (int i = 0; i < MAX_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    reduce();

    return 0;
}
