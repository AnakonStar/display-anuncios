"use client";
import { useCallback, useState } from "react";

/**
 * Custom hook to manage the state of a modal.
 * @param initialState - Initial state of the modal (open or closed).
 * @returns An object containing the modal state and functions to open, close, and toggle the modal.
 */

export interface UseModalReturn<T = any> {
  isOpen: boolean;
  openModal: () => void;
  closeModal: () => void;
  toggleModal: () => void;
  actualDataInModal: T | null;
  setActualDataInModal: (data: T | null) => void;
}

export function useModal<T = any>(initialState: boolean = false) {
  const [isOpen, setIsOpen] = useState(initialState);
  const [actualDataInModal, setActualDataInModal] = useState<T | null>(null);

  const openModal = useCallback(() => setIsOpen(true), []);
  const closeModal = useCallback(() => setIsOpen(false), []);
  const toggleModal = useCallback(() => setIsOpen((prev) => !prev), []);

  return { isOpen, openModal, closeModal, toggleModal, actualDataInModal, setActualDataInModal };
};
