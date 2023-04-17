import { runSequent, arrayChangeDelete } from "./lab4";

describe("runSequent", () => {
    it("should return an empty array when given an empty array", async () => {
        const result = await runSequent([], async () => {});
        expect(result).toEqual([]);
    });

    it("should call the callback for each item in the array", async () => {
        const mockCallback = jest.fn();
        await runSequent([1, 2, 3], mockCallback);
        expect(mockCallback).toHaveBeenCalledTimes(3);
    });

    it("should pass each item and its index to the callback", async () => {
        const mockCallback = jest.fn();
        await runSequent(["one", "two", "three"], mockCallback);
        expect(mockCallback).toHaveBeenNthCalledWith(1, "one", 0);
        expect(mockCallback).toHaveBeenNthCalledWith(2, "two", 1);
        expect(mockCallback).toHaveBeenNthCalledWith(3, "three", 2);
    });

    it("should return an array of results from the callback", async () => {
        const result = await runSequent([1, 2, 3], async (item, index) => item + index);
        expect(result).toEqual([1, 3, 5]);
    });
});

describe("arrayChangeDelete", () => {
    it("should remove even numbers from an array of numbers", () => {
        const array = [1, 2, 3, 6, 7, 9];
        const expectedArray = [1, 3, 7, 9];
        const expectedDeletedElements = [2, 6];

        const deletedElements = arrayChangeDelete(array, (item) => item % 2 === 0);

        expect(array).toEqual(expectedArray);
        expect(deletedElements).toEqual(expectedDeletedElements);
    });

    it("should remove strings which contain vowels from an array of strings", () => {
        const array = ["apple", "banana", "js", "ts", "orange"];
        const expectedArray = ["js", "ts"];
        const expectedDeletedElements = ["apple", "banana", "orange"];

        const deletedElements = arrayChangeDelete(array, (item) => /[aeiou]/g.test(item));

        expect(array).toEqual(expectedArray);
        expect(deletedElements).toEqual(expectedDeletedElements);
    });
});