import { Text, TouchableOpacity, TouchableOpacityProps } from 'react-native';

type ModalButtonProps = TouchableOpacityProps & {
  title: string;
  className?: string; 
};

export function ModalButton({
  title,
  className = '',
  ...props
}: ModalButtonProps) {
  return (
    <TouchableOpacity
      {...props}
      className={`p-5 border-t border-gray-200 items-center ${className}`}
    >
      <Text className="text-gray-600 text-xl">{title}</Text>
    </TouchableOpacity>
  );
}
