const { MongoClient } = require('mongodb');
const bcrypt = require('bcryptjs');

const uri = 'mongodb://localhost:27017/medly-pharma';
const client = new MongoClient(uri);

async function seedDatabase() {
    try {
        await client.connect();
        console.log('Connected to MongoDB');

        const db = client.db('medly-pharma');

        // Clear existing data
        await db.collection('users').deleteMany({});
        await db.collection('medicines').deleteMany({});
        await db.collection('orders').deleteMany({});
        await db.collection('prescriptions').deleteMany({});
        await db.collection('cart_items').deleteMany({});
        await db.collection('order_items').deleteMany({});

        console.log('Cleared existing data');

        // Hash password function
        const hashPassword = async (password) => {
            const saltRounds = 10;
            return await bcrypt.hash(password, saltRounds);
        };

        // Sample Users
        const users = [
            {
                name: 'John Doe',
                email: 'john.doe@example.com',
                password: await hashPassword('password123'),
                role: 'USER',
                gender: 'Male',
                age: 30,
                phone: '+1234567890',
                address: '123 Main St, City, State 12345',
                profileImage: 'https://via.placeholder.com/150',
                dateOfBirth: new Date('1994-01-15'),
                active: true,
                createdAt: new Date()
            },
            {
                name: 'Jane Smith',
                email: 'jane.smith@example.com',
                password: await hashPassword('password123'),
                role: 'USER',
                gender: 'Female',
                age: 28,
                phone: '+1234567891',
                address: '456 Oak Ave, City, State 12346',
                profileImage: 'https://via.placeholder.com/150',
                dateOfBirth: new Date('1996-03-22'),
                active: true,
                createdAt: new Date()
            },
            {
                name: 'Admin User',
                email: 'admin@medlypharma.com',
                password: await hashPassword('admin123'),
                role: 'ADMIN',
                gender: 'Male',
                age: 35,
                phone: '+1234567892',
                address: '789 Admin Blvd, City, State 12347',
                profileImage: 'https://via.placeholder.com/150',
                dateOfBirth: new Date('1989-07-10'),
                active: true,
                createdAt: new Date()
            },
            {
                name: 'Alice Johnson',
                email: 'alice.johnson@example.com',
                password: await hashPassword('password123'),
                role: 'USER',
                gender: 'Female',
                age: 25,
                phone: '+1234567893',
                address: '321 Pine St, City, State 12348',
                profileImage: 'https://via.placeholder.com/150',
                dateOfBirth: new Date('1999-11-05'),
                active: true,
                createdAt: new Date()
            },
            {
                name: 'Bob Wilson',
                email: 'bob.wilson@example.com',
                password: await hashPassword('password123'),
                role: 'USER',
                gender: 'Male',
                age: 42,
                phone: '+1234567894',
                address: '654 Elm Dr, City, State 12349',
                profileImage: 'https://via.placeholder.com/150',
                dateOfBirth: new Date('1982-09-18'),
                active: true,
                createdAt: new Date()
            }
        ];

        const insertedUsers = await db.collection('users').insertMany(users);
        console.log('Inserted users:', insertedUsers.insertedCount);

        // Sample Medicines
        const medicines = [
            {
                name: 'Aspirin 100mg',
                description: 'Pain relief and anti-inflammatory medication',
                manufacturer: 'Bayer',
                category: 'Pain Relief',
                price: 9.99,
                quantityInStock: 150,
                minimumStockLevel: 10,
                dosage: 'Take 1-2 tablets every 4-6 hours as needed',
                expiryDate: new Date('2025-12-31'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Aspirin',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Ibuprofen 200mg',
                description: 'Non-steroidal anti-inflammatory drug for pain and fever',
                manufacturer: 'Advil',
                category: 'Pain Relief',
                price: 12.50,
                quantityInStock: 200,
                minimumStockLevel: 15,
                dosage: 'Take 1 tablet every 6-8 hours with food',
                expiryDate: new Date('2025-10-15'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Ibuprofen',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Amoxicillin 500mg',
                description: 'Antibiotic for bacterial infections',
                manufacturer: 'Pfizer',
                category: 'Antibiotics',
                price: 25.00,
                quantityInStock: 75,
                minimumStockLevel: 20,
                dosage: 'Take 1 capsule 3 times daily for 7-10 days',
                expiryDate: new Date('2024-08-20'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Amoxicillin',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Lisinopril 10mg',
                description: 'ACE inhibitor for high blood pressure',
                manufacturer: 'Merck',
                category: 'Cardiovascular',
                price: 15.75,
                quantityInStock: 100,
                minimumStockLevel: 12,
                dosage: 'Take 1 tablet daily',
                expiryDate: new Date('2025-06-30'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Lisinopril',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Metformin 500mg',
                description: 'Oral diabetes medicine',
                manufacturer: 'Bristol Myers Squibb',
                category: 'Diabetes',
                price: 8.99,
                quantityInStock: 120,
                minimumStockLevel: 15,
                dosage: 'Take 1-2 tablets with meals',
                expiryDate: new Date('2025-03-15'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Metformin',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Omeprazole 20mg',
                description: 'Proton pump inhibitor for acid reflux',
                manufacturer: 'AstraZeneca',
                category: 'Digestive Health',
                price: 18.50,
                quantityInStock: 90,
                minimumStockLevel: 10,
                dosage: 'Take 1 capsule daily before meals',
                expiryDate: new Date('2025-11-22'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Omeprazole',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Simvastatin 20mg',
                description: 'Statin for cholesterol management',
                manufacturer: 'Merck',
                category: 'Cardiovascular',
                price: 22.00,
                quantityInStock: 85,
                minimumStockLevel: 8,
                dosage: 'Take 1 tablet daily in the evening',
                expiryDate: new Date('2025-09-10'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Simvastatin',
                createdAt: new Date(),
                updatedAt: new Date()
            },
            {
                name: 'Cetirizine 10mg',
                description: 'Antihistamine for allergies',
                manufacturer: 'Johnson & Johnson',
                category: 'Allergy',
                price: 11.25,
                quantityInStock: 140,
                minimumStockLevel: 18,
                dosage: 'Take 1 tablet daily',
                expiryDate: new Date('2025-07-05'),
                imageUrl: 'https://via.placeholder.com/200x200?text=Cetirizine',
                createdAt: new Date(),
                updatedAt: new Date()
            }
        ];

        const insertedMedicines = await db.collection('medicines').insertMany(medicines);
        console.log('Inserted medicines:', insertedMedicines.insertedCount);

        // Sample Prescriptions
        const prescriptions = [
            {
                fileName: 'prescription_john_doe_001.pdf',
                filePath: 'uploads/prescriptions/prescription_john_doe_001.pdf',
                fileType: 'application/pdf',
                contentType: 'application/pdf',
                fileSize: 245760,
                uploadDate: new Date(),
                verified: true,
                userEmail: 'john.doe@example.com',
                patientName: 'John Doe',
                doctorName: 'Dr. Sarah Johnson',
                user: insertedUsers.insertedIds[0]
            },
            {
                fileName: 'prescription_jane_smith_001.pdf',
                filePath: 'uploads/prescriptions/prescription_jane_smith_001.pdf',
                fileType: 'application/pdf',
                contentType: 'application/pdf',
                fileSize: 198656,
                uploadDate: new Date(),
                verified: true,
                userEmail: 'jane.smith@example.com',
                patientName: 'Jane Smith',
                doctorName: 'Dr. Michael Chen',
                user: insertedUsers.insertedIds[1]
            },
            {
                fileName: 'prescription_alice_johnson_001.pdf',
                filePath: 'uploads/prescriptions/prescription_alice_johnson_001.pdf',
                fileType: 'application/pdf',
                contentType: 'application/pdf',
                fileSize: 312576,
                uploadDate: new Date(),
                verified: false,
                userEmail: 'alice.johnson@example.com',
                patientName: 'Alice Johnson',
                doctorName: 'Dr. Robert Davis',
                user: insertedUsers.insertedIds[3]
            }
        ];

        const insertedPrescriptions = await db.collection('prescriptions').insertMany(prescriptions);
        console.log('Inserted prescriptions:', insertedPrescriptions.insertedCount);

        // Update users with prescription references
        await db.collection('users').updateOne(
            { _id: insertedUsers.insertedIds[0] },
            { $push: { prescriptions: insertedPrescriptions.insertedIds[0] } }
        );
        await db.collection('users').updateOne(
            { _id: insertedUsers.insertedIds[1] },
            { $push: { prescriptions: insertedPrescriptions.insertedIds[1] } }
        );
        await db.collection('users').updateOne(
            { _id: insertedUsers.insertedIds[3] },
            { $push: { prescriptions: insertedPrescriptions.insertedIds[2] } }
        );

        // Sample Order Items
        const orderItems = [
            {
                medicine: insertedMedicines.insertedIds[0],
                medicineId: insertedMedicines.insertedIds[0].toString(),
                medicineName: 'Aspirin 100mg',
                quantity: 2,
                unitPrice: 9.99
            },
            {
                medicine: insertedMedicines.insertedIds[1],
                medicineId: insertedMedicines.insertedIds[1].toString(),
                medicineName: 'Ibuprofen 200mg',
                quantity: 1,
                unitPrice: 12.50
            },
            {
                medicine: insertedMedicines.insertedIds[2],
                medicineId: insertedMedicines.insertedIds[2].toString(),
                medicineName: 'Amoxicillin 500mg',
                quantity: 3,
                unitPrice: 25.00
            },
            {
                medicine: insertedMedicines.insertedIds[3],
                medicineId: insertedMedicines.insertedIds[3].toString(),
                medicineName: 'Lisinopril 10mg',
                quantity: 1,
                unitPrice: 15.75
            },
            {
                medicine: insertedMedicines.insertedIds[4],
                medicineId: insertedMedicines.insertedIds[4].toString(),
                medicineName: 'Metformin 500mg',
                quantity: 2,
                unitPrice: 8.99
            }
        ];

        const insertedOrderItems = await db.collection('order_items').insertMany(orderItems);
        console.log('Inserted order items:', insertedOrderItems.insertedCount);

        // Sample Orders
        const orders = [
            {
                user: insertedUsers.insertedIds[0],
                userEmail: 'john.doe@example.com',
                orderNumber: 'ORD-2024-001',
                orderDate: new Date(),
                createdAt: new Date(),
                totalAmount: 57.48, // 2*9.99 + 1*12.50 + 3*25.00
                shippingAddress: '123 Main St, City, State 12345',
                paymentMethod: 'Credit Card',
                status: 'DELIVERED',
                items: [insertedOrderItems.insertedIds[0], insertedOrderItems.insertedIds[1], insertedOrderItems.insertedIds[2]],
                prescription: insertedPrescriptions.insertedIds[0]
            },
            {
                user: insertedUsers.insertedIds[1],
                userEmail: 'jane.smith@example.com',
                orderNumber: 'ORD-2024-002',
                orderDate: new Date(),
                createdAt: new Date(),
                totalAmount: 24.74, // 1*15.75 + 2*8.99
                shippingAddress: '456 Oak Ave, City, State 12346',
                paymentMethod: 'PayPal',
                status: 'SHIPPED',
                items: [insertedOrderItems.insertedIds[3], insertedOrderItems.insertedIds[4]],
                prescription: insertedPrescriptions.insertedIds[1]
            },
            {
                user: insertedUsers.insertedIds[3],
                userEmail: 'alice.johnson@example.com',
                orderNumber: 'ORD-2024-003',
                orderDate: new Date(),
                createdAt: new Date(),
                totalAmount: 9.99,
                shippingAddress: '321 Pine St, City, State 12348',
                paymentMethod: 'Credit Card',
                status: 'ORDERED',
                items: [insertedOrderItems.insertedIds[0]],
                prescription: null
            }
        ];

        const insertedOrders = await db.collection('orders').insertMany(orders);
        console.log('Inserted orders:', insertedOrders.insertedCount);

        // Sample Cart Items
        const cartItems = [
            {
                userId: insertedUsers.insertedIds[0].toString(),
                medicineId: insertedMedicines.insertedIds[5].toString(),
                medicineName: 'Omeprazole 20mg',
                imageUrl: 'https://via.placeholder.com/200x200?text=Omeprazole',
                quantity: 1,
                price: 18.50
            },
            {
                userId: insertedUsers.insertedIds[1].toString(),
                medicineId: insertedMedicines.insertedIds[6].toString(),
                medicineName: 'Simvastatin 20mg',
                imageUrl: 'https://via.placeholder.com/200x200?text=Simvastatin',
                quantity: 2,
                price: 22.00
            },
            {
                userId: insertedUsers.insertedIds[4].toString(),
                medicineId: insertedMedicines.insertedIds[7].toString(),
                medicineName: 'Cetirizine 10mg',
                imageUrl: 'https://via.placeholder.com/200x200?text=Cetirizine',
                quantity: 1,
                price: 11.25
            }
        ];

        const insertedCartItems = await db.collection('cart_items').insertMany(cartItems);
        console.log('Inserted cart items:', insertedCartItems.insertedCount);

        console.log('\n=== SEEDING COMPLETED SUCCESSFULLY ===');
        console.log('Summary:');
        console.log(`- Users: ${insertedUsers.insertedCount}`);
        console.log(`- Medicines: ${insertedMedicines.insertedCount}`);
        console.log(`- Prescriptions: ${insertedPrescriptions.insertedCount}`);
        console.log(`- Orders: ${insertedOrders.insertedCount}`);
        console.log(`- Order Items: ${insertedOrderItems.insertedCount}`);
        console.log(`- Cart Items: ${insertedCartItems.insertedCount}`);

        console.log('\nSample login credentials:');
        console.log('Admin: admin@medlypharma.com / admin123');
        console.log('User: john.doe@example.com / password123');
        console.log('User: jane.smith@example.com / password123');

    } catch (error) {
        console.error('Error seeding database:', error);
    } finally {
        await client.close();
        console.log('Database connection closed');
    }
}

seedDatabase().catch(console.error);
