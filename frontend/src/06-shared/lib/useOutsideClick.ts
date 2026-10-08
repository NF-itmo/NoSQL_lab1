import { RefObject, useEffect, useRef } from "react";

export const useOutsideClick = <T extends HTMLElement>(
  onClick: () => void,
  excludeRef?: RefObject<HTMLElement | null>
): RefObject<T|null> => {
  const ref = useRef<T>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;
      
      const isOutsideMenu = ref.current && !ref.current.contains(target);
      const isOnExcludedElement = excludeRef?.current && excludeRef.current.contains(target);

      if (isOutsideMenu && !isOnExcludedElement) {
        onClick();
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [onClick, excludeRef]);

  return ref;
};