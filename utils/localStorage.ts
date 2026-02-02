import { StorageCollectionEnum } from "@/enums/CollectionEnum";
import AsyncStorage from "@react-native-async-storage/async-storage";

async function getLocalStorageItem<T>(key: StorageCollectionEnum): Promise<T | null> {
    if (typeof window !== "undefined") {
        const item = await AsyncStorage.getItem(key);
        if (item) {
            try {
                return JSON.parse(item);
            } catch (error) {
                console.error(`Error parsing localStorage item "${key}":`, error);
            }
        }
    }
    return null;
}

async function setLocalStorageItem<T>(key: StorageCollectionEnum, value: T): Promise<void> {
    if (typeof window !== "undefined") {
        try {
            await AsyncStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error(`Error setting localStorage item "${key}":`, error);
        }
    }
}

async function removeLocalStorageItem(key: StorageCollectionEnum): Promise<void> {
    if (typeof window !== "undefined") {
        try {
            await AsyncStorage.removeItem(key);
        } catch (error) {
            console.error(`Error removing localStorage item "${key}":`, error);
        }
    }
}

export const LocalStorageService = {
    getItem:getLocalStorageItem,
    setItem:setLocalStorageItem,
    removeItem:removeLocalStorageItem,
};