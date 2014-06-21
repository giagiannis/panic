import random
import string

__author__ = 'Giannis Giannakopoulos'


def get_random_file_name(size=20):
    """
    This function returns a random file name
    """
    picks = string.lowercase[:26] + "1"+"2"+"3"
    return ''.join(random.choice(picks) for _ in range(1, size + 1))