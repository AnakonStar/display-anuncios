import { ModalProps, Modal as ModalRC, Text } from 'react-native';

export function ChangeTvCodeModal(props: ModalProps) {
  return (
    <ModalRC {...props} animationType="fade">
      <Text>Alterar código da TV</Text>
    </ModalRC>
  );
}
