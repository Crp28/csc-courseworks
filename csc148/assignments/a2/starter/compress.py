"""
Assignment 2 starter code
CSC148, Winter 2022
Instructors: Bogdan Simion, Sonya Allin, and Pooja Vashisth

This code is provided solely for the personal and private use of
students taking the CSC148 course at the University of Toronto.
Copying for purposes other than this use is expressly prohibited.
All forms of distribution of this code, whether as given or with
any changes, are expressly prohibited.

All of the files in this directory and all subdirectories are:
Copyright (c) 2022 Bogdan Simion, Dan Zingaro
"""
from __future__ import annotations

import time

from huffman import HuffmanTree
from utils import *
from typing import *


# ====================
# Functions for compression


def build_frequency_dict(text: bytes) -> dict[int, int]:
    """ Return a dictionary which maps each of the bytes in <text> to its
    frequency.

    >>> d = build_frequency_dict(bytes([65, 66, 67, 66]))
    >>> d == {65: 1, 66: 2, 67: 1}
    True
    """
    d = {}
    for item in text:
        if item not in d.keys():
            d[item] = 1
        else:
            d[item] += 1
    return d


def build_huffman_tree(freq_dict: dict[int, int]) -> HuffmanTree:
    """ Return the Huffman tree corresponding to the frequency dictionary
    <freq_dict>.

    Precondition: freq_dict is not empty.

    >>> freq = {2: 6, 3: 4}
    >>> t = build_huffman_tree(freq)
    >>> result = HuffmanTree(None, HuffmanTree(3), HuffmanTree(2))
    >>> t == result
    True
    >>> freq = {2: 6, 3: 4, 7: 5}
    >>> t = build_huffman_tree(freq)
    >>> result = HuffmanTree(None, HuffmanTree(2), \
                             HuffmanTree(None, HuffmanTree(3), HuffmanTree(7)))
    >>> t == result
    True
    >>> import random
    >>> symbol = random.randint(0,255)
    >>> freq = {symbol: 6}
    >>> t = build_huffman_tree(freq)
    >>> any_valid_byte_other_than_symbol = (symbol + 1) % 256
    >>> dummy_tree = HuffmanTree(any_valid_byte_other_than_symbol)
    >>> result = HuffmanTree(None, HuffmanTree(symbol), dummy_tree)
    >>> t.left == result.left or t.right == result.left
    True
    """
    freq_dict1 = {}
    for k, v in freq_dict.items():
        freq_dict1[k] = [None, v]
    result = build_helper(freq_dict1)
    return result


def build_helper(freq_dict: dict[Union[int, str], list]) \
        -> HuffmanTree:
    # helper for build_huffman_tree
    # if key is string, the entry is a combined HuffmanTree
    # value = [Optional[HuffmanTree], frequency]

    if len(freq_dict) == 1:
        item = list(freq_dict)[0], freq_dict[list(freq_dict)[0]]

        # normal base case
        if isinstance(item[0], str):
            return item[1][0]

        # special: one symbol in freq_dict and dummy
        else:
            dummy = item[0] + 1
            return HuffmanTree(None, HuffmanTree(item[0]), HuffmanTree(dummy))

    else:
        # find the least two frequencies in dict
        # includes checking on equal frequencies
        # post-condition: least's frequency < least_2's frequency
        least = list(freq_dict)[0], freq_dict[list(freq_dict)[0]]
        least_2 = list(freq_dict)[1], freq_dict[list(freq_dict)[1]]
        if least_2[1][1] < least[1][1]:
            temp = least
            least = least_2
            least_2 = temp
        elif least_2[1][1] == least[1][1]:
            if not isinstance(least_2[0], str) and \
                    isinstance(least[0], str):
                temp = least
                least = least_2
                least_2 = temp
        for item in freq_dict:
            if item == least[0] or item == least[1]:
                continue
            if freq_dict[item][1] < least_2[1][1]:
                least_2 = item, freq_dict[item]
                if least_2[1][1] < least[1][1]:
                    temp = least
                    least = least_2
                    least_2 = temp
            elif freq_dict[item][1] == least_2[1][1]:
                # if one is compound
                if not isinstance(item, str) and \
                        isinstance(least_2[0], str):
                    least_2 = item, freq_dict[item]
                    if least_2[1] < least[1]:
                        temp = least
                        least = least_2
                        least_2 = temp

        # form a new HuffmanTree
        # the compound tree is putted back at the end of dict and has the newly
        # formed Tree as key and frequency sum(freq1, freq2)
        if not isinstance(least[0], str) and \
                not isinstance(least_2[0], str):
            new = HuffmanTree(None, HuffmanTree(least[0]),
                              HuffmanTree(least_2[0]))
            new_freq = least[1][1] + least_2[1][1]
        elif isinstance(least[0], str) and \
                not isinstance(least_2[0], str):
            new = HuffmanTree(None, least[1][0], HuffmanTree(least_2[0]))
            new_freq = least[1][1] + least_2[1][1]
        elif not isinstance(least[0], str) and \
                isinstance(least_2[0], str):
            new = HuffmanTree(None, HuffmanTree(least[0]), least_2[1][0])
            new_freq = least[1][1] + least_2[1][1]
        else:
            new = HuffmanTree(None, least[1][0], least_2[1][0])
            new_freq = least[1][1] + least_2[1][1]
        # put the newly formed tree and its freq in dict
        freq_dict[new.__repr__()] = [new, new_freq]
        freq_dict.pop(least[0])
        freq_dict.pop(least_2[0])
        return build_helper(freq_dict)


def get_codes(tree: HuffmanTree) -> dict[int, str]:
    """ Return a dictionary which maps symbols from the Huffman tree <tree>
    to codes.

    >>> tree = HuffmanTree(None, HuffmanTree(3), HuffmanTree(2))
    >>> d = get_codes(tree)
    >>> d == {3: "0", 2: "1"}
    True
    >>> left = HuffmanTree(None,\
     HuffmanTree(None, HuffmanTree(3)), HuffmanTree(2))
    >>> right = HuffmanTree(None, HuffmanTree(18), HuffmanTree(9))
    >>> tree = HuffmanTree(None, left, right)
    >>> d = get_codes(tree)
    >>> d == {3: "000", 2: "01", 18: "10", 9: "11"}
    True
    """
    temp = ''
    return get_code_helper(tree, temp)


def get_code_helper(tree: HuffmanTree, curr_code: str) -> dict[int, str]:
    if not tree:
        return {}
    if tree.is_leaf():
        if curr_code == '':
            return {}
        return {tree.symbol: curr_code}
    else:
        left = get_code_helper(tree.left, curr_code + '0')
        right = get_code_helper(tree.right, curr_code + '1')
        return left | right


def number_nodes(tree: HuffmanTree) -> None:
    """ Number internal nodes in <tree> according to postorder traversal. The
    numbering starts at 0.

    >>> left = HuffmanTree(None, HuffmanTree(3), HuffmanTree(2))
    >>> right = HuffmanTree(None, HuffmanTree(9), HuffmanTree(10))
    >>> tree = HuffmanTree(None, left, right)
    >>> number_nodes(tree)
    >>> tree.left.number
    0
    >>> tree.right.number
    1
    >>> tree.number
    2
    >>> left = HuffmanTree(None, HuffmanTree(3, None, None), \
    HuffmanTree(2, None, None))
    >>> right = HuffmanTree(5)
    >>> tree = HuffmanTree(None, left, right)
    >>> number_nodes(tree)
    >>> tree.left.number
    0
    >>> tree.number
    1
    """
    number = 0

    # do a postorder traversal
    post_numbering(tree, number)


def post_numbering(tree: HuffmanTree, curr_num: int) -> int:
    """helper for number_nodes. returns number after numbering"""
    if not tree:
        return curr_num
    if tree.is_leaf():
        return curr_num
    else:
        # left
        curr_num = post_numbering(tree.left, curr_num)
        # right
        curr_num = post_numbering(tree.right, curr_num)
        # root
        tree.number = curr_num

        return curr_num + 1


def avg_length(tree: HuffmanTree, freq_dict: dict[int, int]) -> float:
    """ Return the average number of bits required per symbol, to compress the
    text made of the symbols and frequencies in <freq_dict>, using the Huffman
    tree <tree>.

    The average number of bits = the weighted sum of the length of each symbol
    (where the weights are given by the symbol's frequencies), divided by the
    total of all symbol frequencies.

    >>> freq = {3: 2, 2: 7, 9: 1}
    >>> left = HuffmanTree(None, HuffmanTree(3), HuffmanTree(2))
    >>> right = HuffmanTree(9)
    >>> tree = HuffmanTree(None, left, right)
    >>> avg_length(tree, freq)  # (2*2 + 7*2 + 1*1) / (2 + 7 + 1)
    1.9
    >>> freq = {3: 2, 2: 7, 9: 1, 18: 32}
    >>> left = HuffmanTree(None,\
     HuffmanTree(None, HuffmanTree(3)), HuffmanTree(2))
    >>> right = HuffmanTree(None, HuffmanTree(18), HuffmanTree(9))
    >>> tree = HuffmanTree(None, left, right)
    >>> avg_length(tree, freq)
    2.0476190476190474
    """
    length_dict = avg_length_helper(tree, 0)
    total = 0
    total_freq = 0
    for item in freq_dict:
        total += freq_dict[item] * length_dict[item]
        total_freq += freq_dict[item]
    return total / total_freq


def avg_length_helper(tree, depth) -> Dict:
    """helper for avg_length(). returns dictionary with symbol as key
    and that leaf's depth as value"""

    length_dict = {}
    if not tree:
        return {}
    if tree.is_leaf():
        length_dict[tree.symbol] = depth
        return length_dict
    else:
        left = avg_length_helper(tree.left, depth + 1)
        right = avg_length_helper(tree.right, depth + 1)
        return left | right


def compress_bytes(text: bytes, codes: dict[int, str]) -> bytes:
    """ Return the compressed form of <text>, using the mapping from <codes>
    for each symbol.

    >>> d = {0: "0", 1: "10", 2: "11"}
    >>> text = bytes([1, 2, 1, 0])
    >>> result = compress_bytes(text, d)
    >>> result == bytes([184])
    True
    >>> [byte_to_bits(byte) for byte in result]
    ['10111000']
    >>> text = bytes([1, 2, 1, 0, 2])
    >>> result = compress_bytes(text, d)
    >>> [byte_to_bits(byte) for byte in result]
    ['10111001', '10000000']
    """
    representation = ''
    for item in text:
        representation += codes[item]
    if len(representation) % 8:
        representation += '0' * (8 - (len(representation) % 8))
    lst = [representation[i: i + 8] for i in range(0, len(representation), 8)]
    lst = [bits_to_byte(item) for item in lst]
    return bytes(lst)


def tree_to_bytes(tree: HuffmanTree) -> bytes:
    """ Return a bytes representation of the Huffman tree <tree>.
    The representation should be based on the postorder traversal of the tree's
    internal nodes, starting from 0.

    Precondition: <tree> has its nodes numbered.

    >>> tree = HuffmanTree(None, HuffmanTree(3, None, None), \
    HuffmanTree(2, None, None))
    >>> number_nodes(tree)
    >>> list(tree_to_bytes(tree))
    [0, 3, 0, 2]
    >>> left = HuffmanTree(None, HuffmanTree(3, None, None), \
    HuffmanTree(2, None, None))
    >>> right = HuffmanTree(5)
    >>> tree = HuffmanTree(None, left, right)
    >>> number_nodes(tree)
    >>> list(tree_to_bytes(tree))
    [0, 3, 0, 2, 1, 0, 0, 5]
    >>> tree = build_huffman_tree(build_frequency_dict(b"helloworld"))
    >>> number_nodes(tree)
    >>> list(tree_to_bytes(tree))\
            #doctest: +NORMALIZE_WHITESPACE
    [0, 104, 0, 101, 0, 119, 0, 114, 1, 0, 1, 1, 0, 100, 0, 111, 0, 108,\
    1, 3, 1, 2, 1, 4]
    """
    number = 0
    return tree_to_bytes_helper_(tree, number)


def tree_to_bytes_helper(tree: HuffmanTree, curr_num: int) -> (List, int):
    """helper for function tree_to_bytes"""
    if not tree:
        return [], curr_num
    if tree.is_leaf():
        return [], curr_num
    elif tree.number == curr_num:
        lst = []
        if tree.left.is_leaf():
            lst.append(0)
            lst.append(tree.left.symbol)
        else:
            lst.append(1)
            lst.append(tree.left.number)
        if tree.right.is_leaf():
            lst.append(0)
            lst.append(tree.right.symbol)
        else:
            lst.append(1)
            lst.append(tree.right.number)
        return lst, curr_num + 1
    else:
        lst = []
        left, num = tree_to_bytes_helper(tree.left, curr_num)
        lst += left
        right, num = tree_to_bytes_helper(tree.right, num)
        lst += right
        root, num = tree_to_bytes_helper(tree, num)
        lst += root
        return lst, num


def tree_to_bytes_helper_(tree: HuffmanTree, curr_num: int) -> bytes:
    """helper for function tree_to_bytes"""
    lst = tree_to_bytes_helper(tree, curr_num)[0]
    return bytes(lst)


def compress_file(in_file: str, out_file: str) -> None:
    """ Compress contents of the file <in_file> and store results in <out_file>.
    Both <in_file> and <out_file> are string objects representing the names of
    the input and output files.

    Precondition: The contents of the file <in_file> are not empty.
    """
    with open(in_file, "rb") as f1:
        text = f1.read()
    freq = build_frequency_dict(text)
    tree = build_huffman_tree(freq)
    codes = get_codes(tree)
    number_nodes(tree)
    print("Bits per symbol:", avg_length(tree, freq))
    result = (tree.num_nodes_to_bytes() + tree_to_bytes(tree)
              + int32_to_bytes(len(text)))
    result += compress_bytes(text, codes)
    with open(out_file, "wb") as f2:
        f2.write(result)


# ====================
# Functions for decompression

def generate_tree_general(node_lst: list[ReadNode],
                          root_index: int) -> HuffmanTree:
    """ Return the Huffman tree corresponding to node_lst[root_index].
    The function assumes nothing about the order of the tree nodes in the list.

    >>> lst = [ReadNode(0, 5, 0, 7), ReadNode(0, 10, 0, 12), \
    ReadNode(1, 1, 1, 0)]
    >>> generate_tree_general(lst, 2)
    HuffmanTree(None, HuffmanTree(None, HuffmanTree(10, None, None), \
HuffmanTree(12, None, None)), \
HuffmanTree(None, HuffmanTree(5, None, None), HuffmanTree(7, None, None)))
    """
    # if type == 0, find the corresponding ReadNode in list that
    # has index == data.
    # For example, a ReadNode(1, 10, 1, 8) means that it has left child at
    # node_lst[10] and right child at node_lst[8]
    root = node_lst[root_index]
    left = None
    right = None
    if root.l_type == 0:
        left = HuffmanTree(root.l_data)
    elif root.l_type == 1:
        left = generate_tree_general(node_lst, root.l_data)
    if root.r_type == 0:
        right = HuffmanTree(root.r_data)
    elif root.r_type == 1:
        right = generate_tree_general(node_lst, root.r_data)
    return HuffmanTree(None, left, right)


def generate_tree_postorder(node_lst: list[ReadNode],
                            root_index: int) -> HuffmanTree:
    """ Return the Huffman tree corresponding to node_lst[root_index].
    The function assumes that the list represents a tree in postorder.

    >>> lst = [ReadNode(0, 5, 0, 7), ReadNode(0, 10, 0, 12), \
    ReadNode(1, 0, 1, 0)]
    >>> generate_tree_postorder(lst, 2)
    HuffmanTree(None, HuffmanTree(None, HuffmanTree(5, None, None), \
HuffmanTree(7, None, None)), \
HuffmanTree(None, HuffmanTree(10, None, None), HuffmanTree(12, None, None)))
    """
    return generate_post_helper(node_lst, root_index)[0]


def generate_post_helper(node_lst: list[ReadNode],
                         root_index: int) -> (HuffmanTree, int):
    # returns the newly crafted tree and the leaf's index.
    # check the returned index to see if right side is finished and start left
    # side with node_lst[returned_index - 1]
    if node_lst[root_index].l_type == 0 and node_lst[root_index].r_type == 0:
        return HuffmanTree(None, HuffmanTree(node_lst[root_index].l_data),
                           HuffmanTree(node_lst[root_index].r_data)), root_index
    elif node_lst[root_index].r_type == 1 and node_lst[root_index].l_type == 0:
        result = generate_post_helper(node_lst, root_index - 1)
        right = result[0]
        ind = result[1]
        return HuffmanTree(None,
                           HuffmanTree(node_lst[root_index].l_data), right), ind
    elif node_lst[root_index].l_type == 1 and node_lst[root_index].r_type == 0:
        result = generate_post_helper(node_lst, root_index - 1)
        left = result[0]
        ind = result[1]
        return HuffmanTree(None,
                           left, HuffmanTree(node_lst[root_index].r_data)), ind
    elif node_lst[root_index].l_type == 1 and node_lst[root_index].r_type == 1:
        right, ind = generate_post_helper(node_lst, root_index - 1)
        left, ind = generate_post_helper(node_lst, ind - 1)
        return HuffmanTree(None, left, right), ind


def decompress_bytes(tree: HuffmanTree, text: bytes, size: int) -> bytes:
    """ Use Huffman tree <tree> to decompress <size> bytes from <text>.

    >>> tree = build_huffman_tree(build_frequency_dict(b'helloworld'))
    >>> number_nodes(tree)
    >>> decompress_bytes(tree, \
             compress_bytes(b'helloworld', get_codes(tree)), len(b'helloworld'))
    b'helloworld'
    """
    lst = []
    codes = get_codes(tree)
    curr = ''
    for bit in text:
        for num in byte_to_bits(bit):
            curr += num
            for k, v in codes.items():
                if v == curr:
                    lst.append(k)
                    curr = ''
    return bytes(lst[:size])


def decompress_file(in_file: str, out_file: str) -> None:
    """ Decompress contents of <in_file> and store results in <out_file>.
    Both <in_file> and <out_file> are string objects representing the names of
    the input and output files.

    Precondition: The contents of the file <in_file> are not empty.
    """
    with open(in_file, "rb") as f:
        num_nodes = f.read(1)[0]
        buf = f.read(num_nodes * 4)
        node_lst = bytes_to_nodes(buf)
        # use generate_tree_general or generate_tree_postorder here
        tree = generate_tree_general(node_lst, num_nodes - 1)
        size = bytes_to_int(f.read(4))
        with open(out_file, "wb") as g:
            text = f.read()
            g.write(decompress_bytes(tree, text, size))


# ====================
# Other functions

def improve_tree(tree: HuffmanTree, freq_dict: dict[int, int]) -> None:
    """ Improve the tree <tree> as much as possible, without changing its shape,
    by swapping nodes. The improvements are with respect to the dictionary of
    symbol frequencies <freq_dict>.

    >>> left = HuffmanTree(None, HuffmanTree(99, None, None), \
    HuffmanTree(100, None, None))
    >>> right = HuffmanTree(None, HuffmanTree(101, None, None), \
    HuffmanTree(None, HuffmanTree(97, None, None), HuffmanTree(98, None, None)))
    >>> tree = HuffmanTree(None, left, right)
    >>> freq = {97: 26, 98: 23, 99: 20, 100: 16, 101: 15}
    >>> avg_length(tree, freq)
    2.49
    >>> improve_tree(tree, freq)
    >>> avg_length(tree, freq)
    2.31
    """
    lst = sorted(freq_dict, key=freq_dict.get)
    sorted_dict = {}
    for item in lst:
        sorted_dict[item] = freq_dict[item]
    left, right = check_left_skew(tree), check_right_skew(tree)
    if left > right:
        skew = 0
    else:
        skew = 1
    new_tree, used_dict = improve_tree_helper(tree, sorted_dict, skew)
    tree.left = new_tree.left
    tree.right = new_tree.right


def improve_tree_helper(tree: HuffmanTree, freq_dict: dict[int, int], skew: int
                        ) -> (HuffmanTree, dict[int, int]):
    """helper for improving tree avg length"""
    if tree.is_leaf():
        tree.symbol = list(freq_dict)[0]
        freq_dict.pop(tree.symbol)
        return tree, freq_dict
    else:
        if skew:
            right, updated_dict = improve_tree_helper(tree.right,
                                                      freq_dict, skew)
            left, updated_dict = improve_tree_helper(tree.left,
                                                     updated_dict, skew)
            tree.left = left
            tree.right = right
        else:
            left, updated_dict = improve_tree_helper(tree.left,
                                                     freq_dict, skew)
            right, updated_dict = improve_tree_helper(tree.right,
                                                      updated_dict, skew)
            tree.left = left
            tree.right = right
        return tree, updated_dict


def check_left_skew(tree: HuffmanTree) -> int:
    """checks which side of the tree has higher depth. returns depth for L."""
    if tree.is_leaf():
        return 1
    else:
        left = 0
        left += check_left_skew(tree.left)
        return left


def check_right_skew(tree: HuffmanTree) -> int:
    """checks which side of the tree has higher depth. returns depth for R."""
    if tree.is_leaf():
        return 1
    else:
        right = 0
        right += check_left_skew(tree.right)
        return right


if __name__ == "__main__":
    import doctest

    doctest.testmod()

    import python_ta

    python_ta.check_all(config={
        'allowed-io': ['compress_file', 'decompress_file'],
        'allowed-import-modules': [
            'python_ta', 'doctest', 'typing', '__future__',
            'time', 'utils', 'huffman', 'random'
        ],
        'disable': ['W0401']
    })

    mode = input(
        "Press c to compress, d to decompress, or other key to exit: ")
    if mode == "c":
        fname = input("File to compress: ")
        start = time.time()
        compress_file(fname, fname + ".huf")
        print(f"Compressed {fname} in {time.time() - start} seconds.")
    elif mode == "d":
        fname = input("File to decompress: ")
        start = time.time()
        decompress_file(fname, fname + ".orig")
        print(f"Decompressed {fname} in {time.time() - start} seconds.")
