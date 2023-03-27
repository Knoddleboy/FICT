// @ts-nocheck
import { add, areAnagrams, deepClone } from "./lab2";

describe("add", () => {
    it("should return a function", () => {
        expect(typeof add(1)).toBe("function");
    });

    it("should add numbers correctly", () => {
        expect(add(1)(2)()).toBe(3);
        expect(add(5)(-2)()).toBe(3);
        expect(add(2)(5)(7)(1)(6)(5)(11)()).toBe(37);
    });
});

describe("areAnagrams", () => {
    it("should return true for valid anagrams", () => {
        expect(areAnagrams("listen", "silent")).toBe(true);
        expect(areAnagrams("admirer", "married")).toBe(true);
        expect(areAnagrams("restful", "fluster")).toBe(true);
    });

    it("should return false for invalid anagrams", () => {
        expect(areAnagrams("hello", "world")).toBe(false);
        expect(areAnagrams("flower", "power")).toBe(false);
        expect(areAnagrams("program", "programmer")).toBe(false);
    });
});

describe("deepClone", () => {
    it("should return null when given null", () => {
        expect(deepClone(null)).toBeNull();
    });

    it("should return the same value for non-object types", () => {
        expect(deepClone(42)).toBe(42);
        expect(deepClone("hello")).toBe("hello");
        expect(deepClone(true)).toBe(true);
    });

    it("should clone a simple object", () => {
        const obj = { a: 1, b: "hello", c: true };
        const clone = deepClone(obj);
        expect(clone).toEqual(obj);
        expect(clone).not.toBe(obj);
    });

    it("should clone an object with nested objects", () => {
        const obj = { a: 1, b: { c: "hello", d: { e: true } } };
        const clone = deepClone(obj);
        expect(clone).toEqual(obj);
        expect(clone).not.toBe(obj);
        expect(clone.b).not.toBe(obj.b);
        expect(clone.b.d).not.toBe(obj.b.d);
    });

    it("should clone an object with arrays", () => {
        const obj = { a: [1, 2, 3], b: [{ c: "hello" }] };
        const clone = deepClone(obj);
        expect(clone).toEqual(obj);
        expect(clone).not.toBe(obj);
        expect(clone.a).not.toBe(obj.a);
        expect(clone.b).not.toBe(obj.b);
        expect(clone.b[0]).not.toBe(obj.b[0]);
    });
});
